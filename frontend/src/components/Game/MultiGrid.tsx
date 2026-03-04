import { useMultiGameStore } from "../../store/multiGameStore";
import { Row } from "./Row";
import styles from "./MultiGrid.module.css";

interface MultiGridProps {
  shakeRow: number | null;
}

export function MultiGrid({ shakeRow }: MultiGridProps) {
  const grids = useMultiGameStore((s) => s.grids);
  const mode = useMultiGameStore((s) => s.mode);
  const attemptsMade = useMultiGameStore((s) => s.attemptsMade);

  return (
    <div className={`${styles.container} ${styles[mode]}`}>
      {grids.map((grid, index) => (
        <div
          key={`${grid.sessionId}-${index}`}
          className={`${styles.grid} ${grid.status !== "PLAYING" ? styles[grid.status] : ""}`}
        >
          {/* Badge de status por grid */}
          {grid.status !== "PLAYING" && (
            <div className={styles.badge}>
              {grid.status === "WON" ? "✓" : (grid.solution ?? "✗")}
            </div>
          )}

          {grid.rows.map((row, ri) => (
            <Row
              key={ri}
              row={row}
              shake={
                shakeRow === ri &&
                ri === attemptsMade &&
                grid.status === "PLAYING"
              }
              compact
              gridIndex={index}
            />
          ))}
        </div>
      ))}
    </div>
  );
}
