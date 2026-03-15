import { GameView } from '@/components/GameView';
import { useAppStore } from '@/store/appStore';
import './App.css';

function App() {
  const { roomId } = useAppStore();

  return (
    <div className="app">
      <header className="app-header">
        <h1>Simulation de vie</h1>
        {roomId && <span className="room-badge">Room: {roomId}</span>}
      </header>
      <main className="app-main">
        <GameView />
      </main>
    </div>
  );
}

export default App;
