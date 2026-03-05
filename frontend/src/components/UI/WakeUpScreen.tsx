import { useEffect, useState } from "react";
import { wakeUpBackend } from "../../utils/wakeUpBackend";
import styles from "./WakeUpScreen.module.css";

type Status = "waking" | "ready" | "error";

interface WakeUpScreenProps {
  children: React.ReactNode;
}

export function WakeUpScreen({ children }: WakeUpScreenProps) {
  const [status, setStatus] = useState<Status>("waking");
  const [attempt, setAttempt] = useState(0);
  const [max, setMax] = useState(20);

  const run = () => {
    setStatus("waking");
    setAttempt(0);
    wakeUpBackend((curr, total) => {
      setAttempt(curr);
      setMax(total);
    }).then((ok) => setStatus(ok ? "ready" : "error"));
  };

  useEffect(() => {
    run();
  }, []);

  if (status === "ready") return <>{children}</>;

  const progress = Math.min((attempt / max) * 100, 100);
  // Estimativa: cada tentativa ~3 s, máx ~60 s
  const secondsLeft = Math.max(0, Math.round((max - attempt) * 3));

  return (
    <div className={styles.overlay}>
      <div className={styles.card}>
        {/* Anel animado */}
        <div className={styles.ringWrap}>
          <svg className={styles.ring} viewBox="0 0 64 64">
            <circle className={styles.ringTrack} cx="32" cy="32" r="28" />
            <circle
              className={styles.ringFill}
              cx="32"
              cy="32"
              r="28"
              style={{
                strokeDashoffset: `${175.9 - (175.9 * progress) / 100}`,
              }}
            />
          </svg>
          {status === "waking" ? (
            <span className={styles.ringIcon}>⚡</span>
          ) : (
            <span className={styles.ringIcon}>⚠</span>
          )}
        </div>

        {status === "waking" && (
          <>
            <h2 className={styles.title}>Acordando servidor</h2>
            <p className={styles.subtitle}>
              O backend está inicializando no Render.
              <br />
              Isso pode levar até <strong>~60 segundos</strong>.
            </p>

            <div className={styles.barWrap}>
              <div
                className={styles.barFill}
                style={{ width: `${progress}%` }}
              />
            </div>

            <p className={styles.meta}>
              {attempt === 0 ? (
                "Conectando…"
              ) : (
                <>
                  Tentativa <span className={styles.accent}>{attempt}</span> /{" "}
                  {max} — ~{secondsLeft}s restantes
                </>
              )}
            </p>

            {/* Dots de "digitando" */}
            <div className={styles.dots}>
              <span />
              <span />
              <span />
            </div>
          </>
        )}

        {status === "error" && (
          <>
            <h2 className={styles.title}>Servidor indisponível</h2>
            <p className={styles.subtitle}>
              Não foi possível conectar após {max} tentativas.
              <br />
              Verifique se o backend está no ar.
            </p>
            <button className={styles.retryBtn} onClick={run}>
              Tentar novamente
            </button>
          </>
        )}
      </div>
    </div>
  );
}
