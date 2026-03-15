/**
 * URL du WebSocket selon l'environnement.
 * En dev : même host que le front (Vite proxy /ws → backend).
 * En prod : même host que le site (Nginx peut proxy /ws vers le backend).
 */
export function getWebSocketUrl(path: string = '/ws/game'): string {
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${protocol}//${window.location.host}${path}`;
}
