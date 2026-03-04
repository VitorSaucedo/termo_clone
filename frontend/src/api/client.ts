import type {
  DailyGameResponse,
  GuessResponse,
  MultiGameResponse,
  MultiGuessResponse,
  StatsResponse,
  GameMode,
} from "../types";

const BASE_URL = import.meta.env.VITE_API_URL ?? "";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });
  if (!res.ok) {
    const problem = await res
      .json()
      .catch(() => ({ detail: "Erro desconhecido." }));
    throw new ApiError(res.status, problem.detail ?? "Erro desconhecido.");
  }
  return res.json() as Promise<T>;
}

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    message: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export const api = {
  // Classic
  getDailyGame: (userId: string): Promise<DailyGameResponse> =>
    request(`/api/game/daily?userId=${encodeURIComponent(userId)}`),

  submitGuess: (sessionId: string, guess: string): Promise<GuessResponse> =>
    request("/api/game/guess", {
      method: "POST",
      body: JSON.stringify({ sessionId, guess }),
    }),

  // Multi-modo
  getMultiGame: (userId: string, mode: GameMode): Promise<MultiGameResponse> =>
    request(
      `/api/game/multi?userId=${encodeURIComponent(userId)}&mode=${mode}`,
    ),

  submitMultiGuess: (
    sessionIds: string[],
    guess: string,
  ): Promise<MultiGuessResponse> =>
    request("/api/game/multi/guess", {
      method: "POST",
      body: JSON.stringify({ sessionIds, guess }),
    }),

  getStats: (userId: string): Promise<StatsResponse> =>
    request(`/api/stats/${encodeURIComponent(userId)}`),
};
