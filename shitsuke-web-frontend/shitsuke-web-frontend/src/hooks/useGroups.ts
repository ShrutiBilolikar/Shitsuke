import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { groupsApi } from '@/services/api/groups.api';
import { streaksApi } from '@/services/api/streaks.api';
import { UserGroupCreateRequest, GroupInviteRequest } from '@/types/api.types';
import toast from 'react-hot-toast';

export const useGroups = () => {
  return useQuery({
    queryKey: ['groups'],
    queryFn: groupsApi.getAll,
  });
};

export const useGroup = (groupId: string) => {
  return useQuery({
    queryKey: ['group', groupId],
    queryFn: () => groupsApi.getById(groupId),
    enabled: !!groupId,
  });
};

export const useGroupMembers = (groupId: string) => {
  return useQuery({
    queryKey: ['group-members', groupId],
    queryFn: () => groupsApi.getMembers(groupId),
    enabled: !!groupId,
  });
};

export const useCreateGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: UserGroupCreateRequest) => groupsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['groups'] });
      toast.success('Group created successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create group');
    },
  });
};

export const useInviteToGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ groupId, request }: { groupId: string; request: GroupInviteRequest }) =>
      groupsApi.inviteUsers(groupId, request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['group-members', variables.groupId] });
      queryClient.invalidateQueries({ queryKey: ['group', variables.groupId] });
      toast.success('Invitations sent successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to send invitations');
    },
  });
};

export const usePendingInvitations = () => {
  return useQuery({
    queryKey: ['pending-invitations'],
    queryFn: groupsApi.getPendingInvitations,
  });
};

export const useAcceptInvitation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (membershipId: string) => groupsApi.acceptInvitation(membershipId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pending-invitations'] });
      queryClient.invalidateQueries({ queryKey: ['groups'] });
      toast.success('Invitation accepted!');
    },
    onError: () => {
      toast.error('Failed to accept invitation');
    },
  });
};

export const useRejectInvitation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (membershipId: string) => groupsApi.rejectInvitation(membershipId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pending-invitations'] });
      toast.success('Invitation rejected');
    },
    onError: () => {
      toast.error('Failed to reject invitation');
    },
  });
};

export const useLeaveGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (groupId: string) => groupsApi.leaveGroup(groupId),
    onSuccess: (_, groupId) => {
      queryClient.invalidateQueries({ queryKey: ['groups'] });
      queryClient.invalidateQueries({ queryKey: ['group', groupId] });
      toast.success('Left group successfully');
    },
    onError: () => {
      toast.error('Failed to leave group');
    },
  });
};

export const useArchiveGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (groupId: string) => groupsApi.archiveGroup(groupId),
    onSuccess: (_, groupId) => {
      queryClient.invalidateQueries({ queryKey: ['groups'] });
      queryClient.invalidateQueries({ queryKey: ['group', groupId] });
      toast.success('Group archived successfully');
    },
    onError: () => {
      toast.error('Failed to archive group');
    },
  });
};

export const useGroupProgress = (groupId: string, date?: string) => {
  return useQuery({
    queryKey: ['group-progress', groupId, date],
    queryFn: () => (date ? groupsApi.getDailyProgress(groupId, date) : groupsApi.getTodayProgress(groupId)),
    enabled: !!groupId,
  });
};

export const useGroupStreak = (groupId: string) => {
  return useQuery({
    queryKey: ['group-streak', groupId],
    queryFn: () => streaksApi.getGroupStreak(groupId),
    enabled: !!groupId,
  });
};

