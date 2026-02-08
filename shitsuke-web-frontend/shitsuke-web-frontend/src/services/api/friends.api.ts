import { apiClient } from './client';
import { FriendshipDto, FriendRequestDto } from '@/types/api.types';

export const friendsApi = {
  getAll: async (): Promise<FriendshipDto[]> => {
    const { data } = await apiClient.get<FriendshipDto[]>('/api/friends');
    return data;
  },

  getPending: async (): Promise<FriendshipDto[]> => {
    const { data } = await apiClient.get<FriendshipDto[]>('/api/friends/pending');
    return data;
  },

  getSent: async (): Promise<FriendshipDto[]> => {
    const { data } = await apiClient.get<FriendshipDto[]>('/api/friends/sent');
    return data;
  },

  sendFriendRequest: async (request: FriendRequestDto): Promise<FriendshipDto> => {
    const { data } = await apiClient.post<FriendshipDto>('/api/friends/request', request);
    return data;
  },

  acceptFriendRequest: async (friendshipId: string): Promise<FriendshipDto> => {
    const { data } = await apiClient.post<FriendshipDto>(`/api/friends/accept/${friendshipId}`);
    return data;
  },

  rejectFriendRequest: async (friendshipId: string): Promise<void> => {
    await apiClient.post(`/api/friends/reject/${friendshipId}`);
  },

  removeFriendship: async (friendshipId: string): Promise<void> => {
    await apiClient.delete(`/api/friends/${friendshipId}`);
  },
};

