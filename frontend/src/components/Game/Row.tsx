import { Tile } from "./Tile";
import type { RowState } from "../../types";
import styles from "./Row.module.css";

interface RowProps {
  row: RowState;
  shake?: boolean;
  compact?: boolean;
  gridIndex?: number;
}

export function Row({
  row,
  shake = false,
  compact = false,
  gridIndex = 0,
}: RowProps) {
  return (
    <div
      className={`${styles.row} ${shake ? styles.shake : ""} ${compact ? styles.compact : ""}`}
    >
      {row.letters.map((letter, i) => (
        <Tile
          key={i}
          index={i}
          letter={letter}
          status={row.statuses[i]}
          isRevealing={row.isRevealing}
          compact={compact}
          gridIndex={gridIndex}
        />
      ))}
    </div>
  );
}
