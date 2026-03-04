import { create } from "zustand";
import { api, ApiError } from "../api/client";
import type { GameStatus, RowState, TileStatus } from "../types";

const WORD_LENGTH = 5;
const MAX_ATTEMPTS = 6;
const USER_ID_KEY = "wordle_user_id";

function getUserId(): string {
  let id = localStorage.getItem(USER_ID_KEY);
  if (!id) {
    id = crypto.randomUUID();
    localStorage.setItem(USER_ID_KEY, id);
  }
  return id;
}

function buildEmptyRows(): RowState[] {
  return Array.from({ length: MAX_ATTEMPTS }, () => ({
    letters: Array(WORD_LENGTH).fill(""),
    statuses: Array<TileStatus>(WORD_LENGTH).fill("empty"),
    isRevealing: false,
  }));
}

interface GameStore {
  userId: string;
  sessionId: string | null;
  solution: string | null;
  rows: RowState[];
  currentRow: number;
  currentInput: string;
  status: GameStatus;
  letterMap: Record<string, TileStatus>;
  toast: string | null;
  // Distingue toasts que devem agitar a linha (erro de input) dos informativos
  toastIsError: boolean;
  isLoading: boolean;
  isModalStatsOpen: boolean;
  isModalHelpOpen: boolean;

  initGame: () => Promise<void>;
  addLetter: (letter: string) => void;
  deleteLetter: () => void;
  submitGuess: () => Promise<void>;
  setToast: (msg: string | null) => void;
  openStats: () => void;
  closeStats: () => void;
  openHelp: () => void;
  closeHelp: () => void;
}

export const useGameStore = create<GameStore>((set, get) => ({
  userId: getUserId(),
  sessionId: null,
  solution: null,
  rows: buildEmptyRows(),
  currentRow: 0,
  currentInput: "",
  status: "PLAYING",
  letterMap: {},
  toast: null,
  toastIsError: false,
  isLoading: false,
  isModalStatsOpen: false,
  isModalHelpOpen: false,

  initGame: async () => {
    set({ isLoading: true });
    try {
      const { userId } = get();
      const data = await api.getDailyGame(userId);

      const rows = buildEmptyRows();
      const letterMap: Record<string, TileStatus> = {};

      data.pastGuesses.forEach((guess, rowIndex) => {
        const letters = guess.toUpperCase().split("");
        const evals = data.pastEvaluations?.[rowIndex] ?? [];
        const statuses = evals.map((e) => e.state as TileStatus);

        rows[rowIndex].letters = letters;
        rows[rowIndex].statuses = statuses;
        rows[rowIndex].isRevealing = false;

        const priority: Record<TileStatus, number> = {
          CORRECT: 3,
          PRESENT: 2,
          ABSENT: 1,
          tbd: 0,
          empty: 0,
        };
        letters.forEach((letter, i) => {
          const current = letterMap[letter];
          if (!current || priority[statuses[i]] > priority[current]) {
            letterMap[letter] = statuses[i];
          }
        });
      });

      set({
        sessionId: data.sessionId,
        currentRow: data.attemptsMade,
        status: data.status,
        rows,
        letterMap,
        isLoading: false,
        // Não exibe toast ao recarregar se o jogo já terminou —
        // o estado do grid já comunica o resultado visualmente.
        toast: null,
        toastIsError: false,
      });
    } catch {
      set({
        isLoading: false,
        toast: "Erro ao carregar o jogo. Tente novamente.",
        toastIsError: false,
      });
    }
  },

  addLetter: (letter: string) => {
    const { currentInput, status } = get();
    if (status !== "PLAYING") return;
    if (currentInput.length >= WORD_LENGTH) return;

    const next = currentInput + letter.toUpperCase();
    set((state) => {
      const rows = [...state.rows];
      const row = { ...rows[state.currentRow] };
      row.letters = [...row.letters];
      row.letters[next.length - 1] = letter.toUpperCase();
      row.statuses = row.statuses.map((_, i) =>
        i < next.length ? "tbd" : "empty",
      ) as TileStatus[];
      rows[state.currentRow] = row;
      return { currentInput: next, rows };
    });
  },

  deleteLetter: () => {
    const { currentInput, status } = get();
    if (status !== "PLAYING") return;
    if (currentInput.length === 0) return;

    const next = currentInput.slice(0, -1);
    set((state) => {
      const rows = [...state.rows];
      const row = { ...rows[state.currentRow] };
      row.letters = [...row.letters];
      row.letters[next.length] = "";
      row.statuses = row.statuses.map((_, i) =>
        i < next.length ? "tbd" : "empty",
      ) as TileStatus[];
      rows[state.currentRow] = row;
      return { currentInput: next, rows };
    });
  },

  submitGuess: async () => {
    const { currentInput, sessionId, currentRow, status } = get();

    if (status !== "PLAYING") return;
    if (currentInput.length < WORD_LENGTH) {
      set({ toast: "Palavra muito curta", toastIsError: true });
      return;
    }
    if (!sessionId) return;

    set({ isLoading: true });

    try {
      const data = await api.submitGuess(sessionId, currentInput);

      set((state) => {
        const rows = [...state.rows];
        const row = { ...rows[currentRow] };

        row.statuses = data.evaluations.map((e) => e.state as TileStatus);
        row.isRevealing = true;
        rows[currentRow] = row;

        const letterMap = { ...state.letterMap };
        const priority: Record<TileStatus, number> = {
          CORRECT: 3,
          PRESENT: 2,
          ABSENT: 1,
          tbd: 0,
          empty: 0,
        };
        data.evaluations.forEach(({ letter, state: s }) => {
          const current = letterMap[letter];
          if (!current || priority[s as TileStatus] > priority[current]) {
            letterMap[letter] = s as TileStatus;
          }
        });

        return {
          rows,
          letterMap,
          currentRow: currentRow + 1,
          currentInput: "",
          status: data.status,
          solution: data.solution,
          isLoading: false,
          toastIsError: false,
        };
      });

      setTimeout(
        () => {
          set((state) => {
            const rows = [...state.rows];
            rows[currentRow] = { ...rows[currentRow], isRevealing: false };
            return { rows };
          });
          const { status: s, solution: sol } = get();
          if (s === "WON") set({ toast: "Excelente! 🎉", toastIsError: false });
          if (s === "LOST")
            set({ toast: `A palavra era: ${sol}`, toastIsError: false });
        },
        WORD_LENGTH * 300 + 400,
      );
    } catch (e) {
      set({ isLoading: false });
      if (e instanceof ApiError) {
        // Palavra inválida (422) é erro de input — deve agitar a linha
        set({ toast: e.message, toastIsError: e.status === 422 });
      } else {
        set({ toast: "Erro ao enviar tentativa.", toastIsError: false });
      }
    }
  },

  setToast: (msg) => set({ toast: msg }),
  openStats: () => set({ isModalStatsOpen: true }),
  closeStats: () => set({ isModalStatsOpen: false }),
  openHelp: () => set({ isModalHelpOpen: true }),
  closeHelp: () => set({ isModalHelpOpen: false }),
}));
