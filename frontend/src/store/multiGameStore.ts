import { create } from "zustand";
import { api, ApiError } from "../api/client";
import type {
  GameMode,
  GameStatus,
  GridState,
  RowState,
  TileStatus,
} from "../types";

const WORD_LENGTH = 5;
const USER_ID_KEY = "wordle_user_id";

function getUserId(): string {
  let id = localStorage.getItem(USER_ID_KEY);
  if (!id) {
    id = crypto.randomUUID();
    localStorage.setItem(USER_ID_KEY, id);
  }
  return id;
}

function buildEmptyRows(max: number): RowState[] {
  return Array.from({ length: max }, () => ({
    letters: Array(WORD_LENGTH).fill(""),
    statuses: Array<TileStatus>(WORD_LENGTH).fill("empty"),
    isRevealing: false,
  }));
}

interface MultiGameStore {
  userId: string;
  mode: GameMode;
  grids: GridState[];
  maxAttempts: number;
  attemptsMade: number;
  currentInput: string;
  globalStatus: GameStatus;
  letterMap: Record<string, TileStatus>;
  toast: string | null;
  toastIsError: boolean;
  isLoading: boolean;

  initGame: (mode: GameMode) => Promise<void>;
  addLetter: (letter: string) => void;
  deleteLetter: () => void;
  submitGuess: () => Promise<void>;
  setToast: (msg: string | null) => void;
}

export const useMultiGameStore = create<MultiGameStore>((set, get) => ({
  userId: getUserId(),
  mode: "DUETO",
  grids: [],
  maxAttempts: 7,
  attemptsMade: 0,
  currentInput: "",
  globalStatus: "PLAYING",
  letterMap: {},
  toast: null,
  toastIsError: false,
  isLoading: false,

  initGame: async (mode) => {
    set({ isLoading: true, mode });
    try {
      const { userId } = get();
      const data = await api.getMultiGame(userId, mode);

      const letterMap: Record<string, TileStatus> = {};
      const priority: Record<TileStatus, number> = {
        CORRECT: 3,
        PRESENT: 2,
        ABSENT: 1,
        tbd: 0,
        empty: 0,
      };

      const grids: GridState[] = data.grids.map((grid) => {
        const rows = buildEmptyRows(data.maxAttempts);

        grid.pastGuesses.forEach((guess, rowIdx) => {
          const letters = guess.toUpperCase().split("");
          const evals = grid.pastEvaluations?.[rowIdx] ?? [];
          const statuses = evals.map((e) => e.state as TileStatus);

          rows[rowIdx].letters = letters;
          rows[rowIdx].statuses = statuses;

          letters.forEach((letter, i) => {
            const cur = letterMap[letter];
            if (!cur || priority[statuses[i]] > priority[cur]) {
              letterMap[letter] = statuses[i];
            }
          });
        });

        return {
          sessionId: grid.sessionId,
          rows,
          status: grid.status,
          solution: null,
        };
      });

      set({
        grids,
        letterMap,
        maxAttempts: data.maxAttempts,
        attemptsMade: data.attemptsMade,
        globalStatus: data.globalStatus,
        isLoading: false,
        // Não exibe toast ao recarregar — o estado dos grids já comunica o resultado.
        toast: null,
        toastIsError: false,
      });
    } catch {
      set({
        isLoading: false,
        toast: "Erro ao carregar o jogo.",
        toastIsError: false,
      });
    }
  },

  addLetter: (letter) => {
    const { currentInput, globalStatus } = get();
    if (globalStatus !== "PLAYING") return;
    if (currentInput.length >= WORD_LENGTH) return;
    const next = currentInput + letter.toUpperCase();

    set((state) => {
      const grids = state.grids.map((grid) => {
        if (grid.status !== "PLAYING") return grid;
        const rows = [...grid.rows];
        const row = { ...rows[state.attemptsMade] };
        row.letters = [...row.letters];
        row.letters[next.length - 1] = letter.toUpperCase();
        row.statuses = row.statuses.map((_, i) =>
          i < next.length ? "tbd" : "empty",
        ) as TileStatus[];
        rows[state.attemptsMade] = row;
        return { ...grid, rows };
      });
      return { currentInput: next, grids };
    });
  },

  deleteLetter: () => {
    const { currentInput, globalStatus } = get();
    if (globalStatus !== "PLAYING") return;
    if (currentInput.length === 0) return;
    const next = currentInput.slice(0, -1);

    set((state) => {
      const grids = state.grids.map((grid) => {
        if (grid.status !== "PLAYING") return grid;
        const rows = [...grid.rows];
        const row = { ...rows[state.attemptsMade] };
        row.letters = [...row.letters];
        row.letters[next.length] = "";
        row.statuses = row.statuses.map((_, i) =>
          i < next.length ? "tbd" : "empty",
        ) as TileStatus[];
        rows[state.attemptsMade] = row;
        return { ...grid, rows };
      });
      return { currentInput: next, grids };
    });
  },

  submitGuess: async () => {
    const { currentInput, grids, attemptsMade, globalStatus } = get();
    if (globalStatus !== "PLAYING") return;
    if (currentInput.length < WORD_LENGTH) {
      set({ toast: "Palavra muito curta", toastIsError: true });
      return;
    }

    set({ isLoading: true });
    try {
      const sessionIds = grids
        .filter((g) => g.status === "PLAYING")
        .map((g) => g.sessionId);

      const data = await api.submitMultiGuess(sessionIds, currentInput);

      const priority: Record<TileStatus, number> = {
        CORRECT: 3,
        PRESENT: 2,
        ABSENT: 1,
        tbd: 0,
        empty: 0,
      };

      set((state) => {
        const letterMap = { ...state.letterMap };
        const newGrids = state.grids.map((grid) => {
          const result = data.gridResults.find(
            (r) => r.sessionId === grid.sessionId,
          );
          if (!result) return grid;

          const rows = [...grid.rows];
          const row = { ...rows[attemptsMade] };
          row.statuses = result.evaluations.map((e) => e.state as TileStatus);
          row.isRevealing = true;
          rows[attemptsMade] = row;

          result.evaluations.forEach(({ letter, state: s }) => {
            const cur = letterMap[letter];
            if (!cur || priority[s as TileStatus] > priority[cur]) {
              letterMap[letter] = s as TileStatus;
            }
          });

          return {
            ...grid,
            rows,
            status: result.status,
            solution: result.solution,
          };
        });

        return {
          grids: newGrids,
          letterMap,
          attemptsMade: data.attemptNumber,
          globalStatus: data.globalStatus,
          currentInput: "",
          isLoading: false,
          toastIsError: false,
        };
      });

      const isQuarteto = grids.length > 2;
      const timeoutMs = isQuarteto ? 2000 : 1500;

      setTimeout(() => {
        set((state) => ({
          grids: state.grids.map((g) => ({
            ...g,
            rows: g.rows.map((row, ri) =>
              ri === attemptsMade ? { ...row, isRevealing: false } : row,
            ),
          })),
        }));
        const { globalStatus: gs, grids: gs2 } = get();
        if (gs === "WON")
          set({
            toast: "Incrível! Todas as palavras! 🎉",
            toastIsError: false,
          });
        if (gs === "LOST") {
          const solutions = gs2.map((g) => g.solution ?? "?").join(", ");
          set({ toast: `As palavras eram: ${solutions}`, toastIsError: false });
        }
      }, timeoutMs);
    } catch (e) {
      set({ isLoading: false });
      set({
        toast: e instanceof ApiError ? e.message : "Erro ao enviar tentativa.",
        toastIsError: e instanceof ApiError && e.status === 422,
      });
    }
  },

  setToast: (msg) => set({ toast: msg }),
}));
