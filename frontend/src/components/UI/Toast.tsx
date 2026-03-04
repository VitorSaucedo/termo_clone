import { useEffect } from "react";
import { useGameStore } from "../../store/gameStore";
import styles from "./Toast.module.css";

/** Desaparece automaticamente após 2s, exceto mensagem de derrota */
export function Toast() {
  const toast = useGameStore((s) => s.toast);
  const status = useGameStore((s) => s.status);
  const setToast = useGameStore((s) => s.setToast);

  useEffect(() => {
    if (!toast) return;
    // Mantém a palavra correta visível por mais tempo
    const delay = status === "LOST" ? 4000 : 2000;
    const timer = setTimeout(() => setToast(null), delay);
    return () => clearTimeout(timer);
  }, [toast, status, setToast]);

  if (!toast) return null;

  return (
    <div className={styles.toast} role="alert" aria-live="polite">
      {toast}
    </div>
  );
}
