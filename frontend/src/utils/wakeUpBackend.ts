const BACKEND_URL =
  import.meta.env.VITE_API_URL ?? "https://termo-clone-backend.onrender.com";
const HEALTH_ENDPOINT = "/actuator/health";
const MAX_ATTEMPTS = 20;
const INTERVAL_MS = 3000;

export async function wakeUpBackend(
  onProgress?: (attempt: number, max: number) => void,
): Promise<boolean> {
  for (let attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
    try {
      const res = await fetch(`${BACKEND_URL}${HEALTH_ENDPOINT}`, {
        signal: AbortSignal.timeout(5000),
      });
      if (res.ok) {
        const data = await res.json();
        if (data?.status === "UP") return true;
      }
    } catch {
      // servidor ainda dormindo, aguarda próxima tentativa
    }
    onProgress?.(attempt, MAX_ATTEMPTS);
    await new Promise((r) => setTimeout(r, INTERVAL_MS));
  }
  return false;
}
