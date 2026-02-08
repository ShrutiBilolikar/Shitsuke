import { apiClient } from './client';
import { StreakDto } from '@/types/api.types';

export const streaksApi = {
  getUserStreak: async (recordTypeId: string): Promise<StreakDto> => {
    const { data } = await apiClient.get<StreakDto>(`/api/streaks/user/${recordTypeId}`);
    return data;
  },
};
