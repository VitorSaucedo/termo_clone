import { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useGameStore } from "./store/gameStore";
import { useKeyboard } from "./hooks/useKeyboard";
import { Grid } from "./components/Game/Grid";
import { Keyboard } from "./components/Keyboard/Keyboard";
import { Toast } from "./components/UI/Toast";
import { StatsModal } from "./components/Modals/StatsModal";
import { HelpModal } from "./components/Modals/HelpModal";
import { Header } from "./components/Layout/Header.tsx";
import { MultiModePage } from "./pages/MultiModePage.tsx";
import { WakeUpScreen } from "./components/UI/WakeUpScreen"; // 👈 novo
import styles from "./App.module.css";

function ClassicPage() {
  const initGame = useGameStore((s) => s.initGame);
  const isLoading = useGameStore((s) => s.isLoading);
  const toast = useGameStore((s) => s.toast);
  const toastIsError = useGameStore((s) => s.toastIsError);
  const currentRow = useGameStore((s) => s.currentRow);
  const [shakeRow, setShakeRow] = useState<number | null>(null);

  useKeyboard();

  useEffect(() => {
    initGame();
  }, []);

  useEffect(() => {
    if (toast && toastIsError) {
      setShakeRow(currentRow);
      setTimeout(() => setShakeRow(null), 600);
    }
  }, [toast, toastIsError]);

  return (
    <>
      <main className={styles.main}>
        {isLoading && <div className={styles.loader} />}
        <Grid shakeRow={shakeRow} />
      </main>
      <footer className={styles.footer}>
        <Keyboard />
      </footer>
      <Toast />
    </>
  );
}

export default function App() {
  const openStats = useGameStore((s) => s.openStats);
  const openHelp = useGameStore((s) => s.openHelp);

  return (
    <BrowserRouter>
      {/* WakeUpScreen envolve tudo: só exibe o app quando o backend estiver UP */}
      <WakeUpScreen>
        <div className={styles.app}>
          <Header onStats={openStats} onHelp={openHelp} />
          <div className={styles.divider} />

          <Routes>
            <Route path="/" element={<ClassicPage />} />
            <Route path="/dueto" element={<MultiModePage mode="DUETO" />} />
            <Route
              path="/quarteto"
              element={<MultiModePage mode="QUARTETO" />}
            />
          </Routes>

          <StatsModal />
          <HelpModal />
        </div>
      </WakeUpScreen>
    </BrowserRouter>
  );
}
