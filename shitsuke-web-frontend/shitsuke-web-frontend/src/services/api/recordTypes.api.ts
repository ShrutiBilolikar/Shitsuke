import { apiClient } from './client';
import { RecordType, RecordTypeRequest } from '@/types/api.types';

export const recordTypesApi = {
  getAll: async (): Promise<RecordType[]> => {
    const { data } = await apiClient.get<RecordType[]>('/api/record-types/all-records');
    return data;
  },

  create: async (request: RecordTypeRequest): Promise<RecordType> => {
    const { data } = await apiClient.post<RecordType>('/api/record-types', request);
    return data;
  },
};
