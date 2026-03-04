import { useGameStore } from "../../store/gameStore";
import type { TileStatus } from "../../types";
import styles from "./Keyboard.module.css";

const ROWS = [
  ["Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"],
  ["A", "S", "D", "F", "G", "H", "J", "K", "L", "Ç"],
  ["ENTER", "Z", "X", "C", "V", "B", "N", "M", "⌫"],
];

interface KeyProps {
  label: string;
  status?: TileStatus;
  onClick: () => void;
}

function Key({ label, status, onClick }: KeyProps) {
  const isWide = label === "ENTER" || label === "⌫";
  return (
    <button
      className={`
        ${styles.key}
        ${isWide ? styles.wide : ""}
        ${status ? styles[status] : ""}
      `}
      onClick={onClick}
      aria-label={label === "⌫" ? "Apagar" : label}
    >
      {label}
    </button>
  );
}

export function Keyboard() {
  const addLetter = useGameStore((s) => s.addLetter);
  const deleteLetter = useGameStore((s) => s.deleteLetter);
  const submitGuess = useGameStore((s) => s.submitGuess);
  const letterMap = useGameStore((s) => s.letterMap);

  function handleKey(key: string) {
    if (key === "ENTER") submitGuess();
    else if (key === "⌫") deleteLetter();
    else addLetter(key);
  }

  return (
    <div className={styles.keyboard}>
      {ROWS.map((row, r) => (
        <div key={r} className={styles.row}>
          {row.map((key) => (
            <Key
              key={key}
              label={key}
              status={letterMap[key]}
              onClick={() => handleKey(key)}
            />
          ))}
        </div>
      ))}
    </div>
  );
}
