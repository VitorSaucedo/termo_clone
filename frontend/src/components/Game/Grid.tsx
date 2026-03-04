import { useGameStore } from "../../store/gameStore";
import { Row } from "./Row";
import styles from "./Grid.module.css";

interface GridProps {
  shakeRow: number | null;
}

export function Grid({ shakeRow }: GridProps) {
  const rows = useGameStore((s) => s.rows);

  return (
    <div className={styles.grid}>
      {rows.map((row, i) => (
        <Row key={i} row={row} shake={shakeRow === i} />
      ))}
    </div>
  );
}
