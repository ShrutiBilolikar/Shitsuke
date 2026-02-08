import { apiClient } from './client';
import { Record, RecordCreateRequest } from '@/types/api.types';

export const recordsApi = {
  getByRecordType: async (recordTypeId: string): Promise<Record[]> => {
    const { data } = await apiClient.get<Record[]>(`/records/record-type/${recordTypeId}`);
    return data;
  },

  create: async (recordTypeId: string, request: RecordCreateRequest): Promise<Record> => {
    const { data } = await apiClient.post<Record>(`/records/${recordTypeId}`, request);
    return data;
  },
};
