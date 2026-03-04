import { useGameStore } from "../../store/gameStore";
import styles from "./Modal.module.css";
import helpStyles from "./HelpModal.module.css";

export function HelpModal() {
  const isOpen = useGameStore((s) => s.isModalHelpOpen);
  const onClose = useGameStore((s) => s.closeHelp);

  if (!isOpen) return null;

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <button className={styles.close} onClick={onClose} aria-label="Fechar">
          ✕
        </button>
        <h2 className={styles.title}>Como Jogar</h2>

        <p className={helpStyles.text}>
          Adivinhe a palavra em <strong>6 tentativas</strong>. Cada tentativa
          deve ser uma palavra válida de 5 letras.
        </p>

        <div className={helpStyles.examples}>
          <Example
            word="TERMO"
            highlight={0}
            state="CORRECT"
            hint="T está na posição correta."
          />
          <Example
            word="CARRO"
            highlight={1}
            state="PRESENT"
            hint="A existe mas está na posição errada."
          />
          <Example
            word="BARCO"
            highlight={3}
            state="ABSENT"
            hint="C não está na palavra."
          />
        </div>
      </div>
    </div>
  );
}

function Example({
  word,
  highlight,
  state,
  hint,
}: {
  word: string;
  highlight: number;
  state: string;
  hint: string;
}) {
  return (
    <div>
      <div style={{ display: "flex", gap: 6, marginBottom: 8 }}>
        {word.split("").map((l, i) => (
          <div
            key={i}
            className={`${helpStyles.exTile} ${i === highlight ? helpStyles[state] : ""}`}
          >
            {l}
          </div>
        ))}
      </div>
      <p className={helpStyles.hint}>{hint}</p>
    </div>
  );
}
