import { useEffect } from "react";
import { useMultiGameStore } from "../store/multiGameStore";

export function useMultiKeyboard() {
  const addLetter = useMultiGameStore((s) => s.addLetter);
  const deleteLetter = useMultiGameStore((s) => s.deleteLetter);
  const submitGuess = useMultiGameStore((s) => s.submitGuess);
  const globalStatus = useMultiGameStore((s) => s.globalStatus);
  const isLoading = useMultiGameStore((s) => s.isLoading);

  useEffect(() => {
    function onKeyDown(e: KeyboardEvent) {
      if (globalStatus !== "PLAYING" || isLoading) return;
      if (e.ctrlKey || e.altKey || e.metaKey) return;
      if (e.key === "Enter") submitGuess();
      else if (e.key === "Backspace") deleteLetter();
      else if (/^[a-zA-ZÀ-ÿ]$/.test(e.key)) addLetter(e.key);
    }
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [globalStatus, isLoading, addLetter, deleteLetter, submitGuess]);
}
