import { useEffect } from "react";
import { useGameStore } from "../store/gameStore";

/**
 * Captura eventos de teclado físico e os encaminha para o store.
 * Montado uma única vez no App — evita múltiplos listeners.
 */
export function useKeyboard() {
  const addLetter = useGameStore((s) => s.addLetter);
  const deleteLetter = useGameStore((s) => s.deleteLetter);
  const submitGuess = useGameStore((s) => s.submitGuess);
  const status = useGameStore((s) => s.status);
  const isLoading = useGameStore((s) => s.isLoading);

  useEffect(() => {
    function onKeyDown(e: KeyboardEvent) {
      if (status !== "PLAYING" || isLoading) return;
      // Ignora combinações com modificadores (Ctrl+C, etc.)
      if (e.ctrlKey || e.altKey || e.metaKey) return;

      if (e.key === "Enter") {
        submitGuess();
      } else if (e.key === "Backspace") {
        deleteLetter();
      } else if (/^[a-zA-ZÀ-ÿ]$/.test(e.key)) {
        addLetter(e.key);
      }
    }

    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [status, isLoading, addLetter, deleteLetter, submitGuess]);
}
