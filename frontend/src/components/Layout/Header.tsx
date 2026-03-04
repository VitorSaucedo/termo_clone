import { NavLink } from "react-router-dom";
import styles from "./Header.module.css";

interface HeaderProps {
  onStats: () => void;
  onHelp: () => void;
}

export function Header({ onStats, onHelp }: HeaderProps) {
  return (
    <header className={styles.header}>
      <button className={styles.iconBtn} onClick={onHelp} aria-label="Ajuda">
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
        >
          <circle cx="12" cy="12" r="10" />
          <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" />
          <line x1="12" y1="17" x2="12.01" y2="17" />
        </svg>
      </button>

      <nav className={styles.nav}>
        <NavLink
          to="/"
          end
          className={({ isActive }) =>
            `${styles.tab} ${isActive ? styles.active : ""}`
          }
        >
          Termo
        </NavLink>
        <NavLink
          to="/dueto"
          className={({ isActive }) =>
            `${styles.tab} ${isActive ? styles.active : ""}`
          }
        >
          Dueto
        </NavLink>
        <NavLink
          to="/quarteto"
          className={({ isActive }) =>
            `${styles.tab} ${isActive ? styles.active : ""}`
          }
        >
          Quarteto
        </NavLink>
      </nav>

      <button
        className={styles.iconBtn}
        onClick={onStats}
        aria-label="Estatísticas"
      >
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
        >
          <line x1="18" y1="20" x2="18" y2="10" />
          <line x1="12" y1="20" x2="12" y2="4" />
          <line x1="6" y1="20" x2="6" y2="14" />
        </svg>
      </button>
    </header>
  );
}
