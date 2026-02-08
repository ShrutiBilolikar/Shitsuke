import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { recordTypesApi } from '@/services/api/recordTypes.api';
import { recordsApi } from '@/services/api/records.api';
import { streaksApi } from '@/services/api/streaks.api';
import { RecordTypeRequest, RecordCreateRequest } from '@/types/api.types';
import toast from 'react-hot-toast';

export const useHabits = () => {
  return useQuery({
    queryKey: ['habits'],
    queryFn: recordTypesApi.getAll,
  });
};

export const useCreateHabit = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: RecordTypeRequest) => recordTypesApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['habits'] });
      toast.success('Habit created successfully!');
    },
    onError: () => {
      toast.error('Failed to create habit');
    },
  });
};

export const useRecords = (recordTypeId: string) => {
  return useQuery({
    queryKey: ['records', recordTypeId],
    queryFn: () => recordsApi.getByRecordType(recordTypeId),
    enabled: !!recordTypeId,
  });
};

export const useCreateRecord = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ recordTypeId, data }: { recordTypeId: string; data: RecordCreateRequest }) =>
      recordsApi.create(recordTypeId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['records', variables.recordTypeId] });
      queryClient.invalidateQueries({ queryKey: ['streak', variables.recordTypeId] });
      toast.success('Record logged successfully!');
    },
    onError: () => {
      toast.error('Failed to log record');
    },
  });
};

export const useStreak = (recordTypeId: string) => {
  return useQuery({
    queryKey: ['streak', recordTypeId],
    queryFn: () => streaksApi.getUserStreak(recordTypeId),
    enabled: !!recordTypeId,
  });
};
