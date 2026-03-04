import { useEffect, useState } from "react";
import { useGameStore } from "../../store/gameStore";
import { api } from "../../api/client";
import type { StatsResponse } from "../../types";
import styles from "./Modal.module.css";
import tabStyles from "./StatsModal.module.css";

type StatsTab = "CLASSIC" | "DUETO" | "QUARTETO";

export function StatsModal() {
  const isOpen = useGameStore((s) => s.isModalStatsOpen);
  const onClose = useGameStore((s) => s.closeStats);
  const userId = useGameStore((s) => s.userId);
  const [stats, setStats] = useState<StatsResponse | null>(null);
  const [activeTab, setActiveTab] = useState<StatsTab>("CLASSIC");

  useEffect(() => {
    if (!isOpen) return;
    api
      .getStats(userId)
      .then(setStats)
      .catch(() => {});
  }, [isOpen, userId]);

  if (!isOpen) return null;

  const tabData = {
    CLASSIC: {
      label: "Termo",
      played: stats?.gamesPlayed ?? 0,
      won: stats?.gamesWon ?? 0,
      winRate: stats?.winRate ?? 0,
      streak: stats?.currentStreak ?? 0,
      maxStreak: stats?.maxStreak ?? 0,
      dist: stats?.guessDistribution ?? {},
      showStreak: true,
    },
    DUETO: {
      label: "Dueto",
      played: stats?.gamesPlayedDueto ?? 0,
      won: stats?.gamesWonDueto ?? 0,
      winRate: stats?.winRateDueto ?? 0,
      streak: null,
      maxStreak: null,
      dist: stats?.guessDistributionDueto ?? {},
      showStreak: false,
    },
    QUARTETO: {
      label: "Quarteto",
      played: stats?.gamesPlayedQuarteto ?? 0,
      won: stats?.gamesWonQuarteto ?? 0,
      winRate: stats?.winRateQuarteto ?? 0,
      streak: null,
      maxStreak: null,
      dist: stats?.guessDistributionQuarteto ?? {},
      showStreak: false,
    },
  };

  const current = tabData[activeTab];
  const maxBar = Math.max(...Object.values(current.dist), 1);

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <button className={styles.close} onClick={onClose} aria-label="Fechar">
          ✕
        </button>
        <h2 className={styles.title}>Estatísticas</h2>

        {/* Abas de modo */}
        <div className={tabStyles.tabs}>
          {(["CLASSIC", "DUETO", "QUARTETO"] as StatsTab[]).map((tab) => (
            <button
              key={tab}
              className={`${tabStyles.tab} ${activeTab === tab ? tabStyles.active : ""}`}
              onClick={() => setActiveTab(tab)}
            >
              {tabData[tab].label}
            </button>
          ))}
        </div>

        {stats && (
          <>
            <div className={styles.summary}>
              <Stat label="Jogadas" value={current.played} />
              <Stat
                label="Vitórias"
                value={`${Math.round(current.winRate)}%`}
              />
              {current.showStreak ? (
                <>
                  <Stat label="Sequência" value={current.streak ?? 0} />
                  <Stat label="Recorde" value={current.maxStreak ?? 0} />
                </>
              ) : (
                <>
                  <Stat label="Ganhos" value={current.won} />
                  {/* espaço vazio para manter o grid de 4 colunas alinhado */}
                  <div />
                </>
              )}
            </div>

            <h3 className={styles.subtitle}>Distribuição</h3>
            <div className={styles.distribution}>
              {Object.entries(current.dist).map(([attempt, count]) => (
                <div key={attempt} className={styles.barRow}>
                  <span className={styles.barLabel}>{attempt}</span>
                  <div
                    className={styles.bar}
                    style={{ width: `${Math.max((count / maxBar) * 100, 8)}%` }}
                  >
                    {count}
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  );
}

function Stat({ label, value }: { label: string; value: string | number }) {
  return (
    <div className={styles.stat}>
      <span className={styles.statValue}>{value}</span>
      <span className={styles.statLabel}>{label}</span>
    </div>
  );
}
