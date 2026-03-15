import { useEffect, useRef } from 'react';
import Phaser from 'phaser';

const config: Phaser.Types.Core.GameConfig = {
  type: Phaser.AUTO,
  width: 800,
  height: 600,
  parent: 'game-container',
  backgroundColor: '#1a1a1e',
  scale: {
    mode: Phaser.Scale.FIT,
    autoCenter: Phaser.Scale.CENTER_BOTH,
  },
  scene: {
    create: function (this: Phaser.Scene) {
      this.add
        .text(400, 300, 'Simulation de vie\n(Phaser + ECS)', {
          fontSize: '24px',
          color: '#e0e0e0',
          align: 'center',
        })
        .setOrigin(0.5);
    },
  },
};

export function GameView() {
  const containerRef = useRef<HTMLDivElement>(null);
  const gameRef = useRef<Phaser.Game | null>(null);

  useEffect(() => {
    if (!containerRef.current) return;
    gameRef.current = new Phaser.Game({ ...config, parent: containerRef.current });
    return () => {
      gameRef.current?.destroy(true);
      gameRef.current = null;
    };
  }, []);

  return <div id="game-container" ref={containerRef} className="game-container" />;
}
