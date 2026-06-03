import api from '../config/api';
import type { Party } from '../types/party';

export const partyService = {
  getAllParties: async (): Promise<Party[]> => {
    const response = await api.get<Party[]>('/parties/all');
    return response.data;
  },

  getPartyById: async (id: number): Promise<Party> => {
    const response = await api.get<Party>(`/parties/${id}`);
    return response.data;
  },

  createParty: async (party: Partial<Party>): Promise<Party> => {
    const response = await api.post<Party>('/parties', party);
    return response.data;
  },

  updateParty: async (id: number, party: Partial<Party>): Promise<Party> => {
    const response = await api.put<Party>(`/parties/${id}`, party);
    return response.data;
  },

  deleteParty: async (id: number): Promise<void> => {
    await api.delete(`/parties/${id}`);
  }
};
