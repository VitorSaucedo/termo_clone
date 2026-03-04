import type { TileStatus } from "../../types";
import styles from "./Tile.module.css";

interface TileProps {
  letter: string;
  status: TileStatus;
  index: number;
  isRevealing: boolean;
  compact?: boolean;
  gridIndex?: number;
}

export function Tile({
  letter,
  status,
  index,
  isRevealing,
  compact = false,
  gridIndex = 0,
}: TileProps) {
  const letterDelay = compact ? 150 : 300;
  const gridDelay = compact ? gridIndex * 250 : 0;
  const animationDelay = `${gridDelay + index * letterDelay}ms`;

  return (
    <div
      className={`
        ${styles.tile}
        ${compact ? styles.compact : ""}
        ${letter ? styles.filled : ""}
        ${isRevealing ? styles.revealing : ""}
      `}
      // O delay controla quando cada tile começa a virar
      style={isRevealing ? { animationDelay } : undefined}
    >
      {/* Front: visível antes da virada, sem cor de estado */}
      <span className={`${styles.front} ${!isRevealing ? styles[status] : ""}`}>
        {letter}
      </span>

      {/* Back: visível depois da virada, com a cor do estado */}
      <span className={`${styles.back} ${styles[status]}`}>{letter}</span>
    </div>
  );
}
