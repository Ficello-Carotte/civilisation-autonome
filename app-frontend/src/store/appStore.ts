import { create } from 'zustand';

interface AppState {
  roomId: string | null;
  setRoomId: (id: string | null) => void;
}

export const useAppStore = create<AppState>((set) => ({
  roomId: null,
  setRoomId: (roomId) => set({ roomId }),
}));
