export type LetterState = "CORRECT" | "PRESENT" | "ABSENT";
export type GameStatus = "PLAYING" | "WON" | "LOST";
export type GameMode = "CLASSIC" | "DUETO" | "QUARTETO";
export type TileStatus = LetterState | "empty" | "tbd";

export interface TileEvaluation {
  letter: string;
  state: LetterState;
}

export interface DailyGameResponse {
  sessionId: string;
  wordLength: number;
  maxAttempts: number;
  attemptsMade: number;
  pastGuesses: string[];
  pastEvaluations: TileEvaluation[][];
  status: GameStatus;
  mode: GameMode;
}

export interface MultiGameResponse {
  mode: GameMode;
  grids: DailyGameResponse[];
  globalStatus: GameStatus;
  attemptsMade: number;
  maxAttempts: number;
}

export interface GuessResponse {
  evaluations: TileEvaluation[];
  attemptNumber: number;
  status: GameStatus;
  solution: string | null;
}

export interface MultiGuessResponse {
  gridResults: GridResult[];
  attemptNumber: number;
  globalStatus: GameStatus;
}

export interface GridResult {
  sessionId: string;
  evaluations: TileEvaluation[];
  status: GameStatus;
  solution: string | null;
}

export interface StatsResponse {
  // CLASSIC
  gamesPlayed: number;
  gamesWon: number;
  winRate: number;
  currentStreak: number;
  maxStreak: number;
  guessDistribution: Record<string, number>;
  // DUETO
  gamesPlayedDueto: number;
  gamesWonDueto: number;
  winRateDueto: number;
  guessDistributionDueto: Record<string, number>;
  // QUARTETO
  gamesPlayedQuarteto: number;
  gamesWonQuarteto: number;
  winRateQuarteto: number;
  guessDistributionQuarteto: Record<string, number>;
}

export interface RowState {
  letters: string[];
  statuses: TileStatus[];
  isRevealing: boolean;
}

export interface GridState {
  sessionId: string;
  rows: RowState[];
  status: GameStatus;
  solution: string | null;
}
