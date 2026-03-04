import { useEffect, useState } from "react";
import type { GameMode } from "../types";
import { useMultiGameStore } from "../store/multiGameStore";
import { useMultiKeyboard } from "../hooks/useMultiKeyboard";
import { MultiGrid } from "../components/Game/MultiGrid";
import styles from "./MultiModePage.module.css";

interface MultiModePageProps {
  mode: GameMode;
}

function MultiKeyboard() {
  const addLetter = useMultiGameStore((s) => s.addLetter);
  const deleteLetter = useMultiGameStore((s) => s.deleteLetter);
  const submitGuess = useMultiGameStore((s) => s.submitGuess);
  const letterMap = useMultiGameStore((s) => s.letterMap);

  const ROWS = [
    ["Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"],
    ["A", "S", "D", "F", "G", "H", "J", "K", "L", "Ç"],
    ["ENTER", "Z", "X", "C", "V", "B", "N", "M", "⌫"],
  ];

  return (
    <div className={styles.keyboard}>
      {ROWS.map((row, r) => (
        <div key={r} className={styles.keyRow}>
          {row.map((key) => {
            const isWide = key === "ENTER" || key === "⌫";
            const status = letterMap[key];
            return (
              <button
                key={key}
                className={`${styles.key} ${isWide ? styles.wide : ""} ${status ? styles[status] : ""}`}
                onClick={() => {
                  if (key === "ENTER") submitGuess();
                  else if (key === "⌫") deleteLetter();
                  else addLetter(key);
                }}
              >
                {key}
              </button>
            );
          })}
        </div>
      ))}
    </div>
  );
}

export function MultiModePage({ mode }: MultiModePageProps) {
  const initGame = useMultiGameStore((s) => s.initGame);
  const isLoading = useMultiGameStore((s) => s.isLoading);
  const toast = useMultiGameStore((s) => s.toast);
  const toastIsError = useMultiGameStore((s) => s.toastIsError);
  const setToast = useMultiGameStore((s) => s.setToast);
  const attemptsMade = useMultiGameStore((s) => s.attemptsMade);
  const maxAttempts = useMultiGameStore((s) => s.maxAttempts);
  const globalStatus = useMultiGameStore((s) => s.globalStatus);

  const [shakeRow, setShakeRow] = useState<number | null>(null);

  useMultiKeyboard();

  useEffect(() => {
    initGame(mode);
  }, [mode]);

  // Agita apenas em erros de input (palavra curta / inválida)
  useEffect(() => {
    if (toast && toastIsError) {
      setShakeRow(attemptsMade);
      setTimeout(() => setShakeRow(null), 600);
    }
  }, [toast, toastIsError]);

  // Toast com auto-dismiss
  useEffect(() => {
    if (!toast) return;
    const t = setTimeout(
      () => setToast(null),
      toast.includes("eram:") ? 5000 : 2500,
    );
    return () => clearTimeout(t);
  }, [toast]);

  const modeLabel = mode === "DUETO" ? "Dueto" : "Quarteto";
  const remainingAttempts = maxAttempts - attemptsMade;

  return (
    <div className={styles.page}>
      <div className={styles.modeInfo}>
        <span className={styles.modeLabel}>{modeLabel}</span>
        <span className={styles.modeDesc}>
          {globalStatus === "PLAYING"
            ? `${remainingAttempts} tentativa${remainingAttempts !== 1 ? "s" : ""} restante${remainingAttempts !== 1 ? "s" : ""}`
            : mode === "DUETO"
              ? "2 palavras · 7 tentativas"
              : "4 palavras · 9 tentativas"}
        </span>
      </div>

      <main className={styles.main}>
        {isLoading && <div className={styles.loader} />}
        <MultiGrid shakeRow={shakeRow} />
      </main>

      <footer className={styles.footer}>
        <MultiKeyboard />
      </footer>

      {toast && (
        <div className={styles.toast} role="alert">
          {toast}
        </div>
      )}
    </div>
  );
}
