import { useEffect, useRef, useState } from 'react';
import { getWebSocketUrl } from '@/utils/getWebSocketUrl';

export type WebSocketStatus = 'connecting' | 'open' | 'closing' | 'closed';

export function useWebSocket(path = '/ws/game') {
  const [status, setStatus] = useState<WebSocketStatus>('closed');
  const [lastMessage, setLastMessage] = useState<string | null>(null);
  const wsRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    const url = getWebSocketUrl(path);
    const ws = new WebSocket(url);
    wsRef.current = ws;
    setStatus('connecting');

    ws.onopen = () => setStatus('open');
    ws.onclose = () => {
      setStatus('closed');
      wsRef.current = null;
    };
    ws.onerror = () => setStatus('closed');
    ws.onmessage = (event) => setLastMessage(event.data);

    return () => {
      setStatus('closing');
      ws.close();
    };
  }, [path]);

  const send = (data: string) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(data);
    }
  };

  return { status, lastMessage, send };
}
