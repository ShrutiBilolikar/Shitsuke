import { apiClient } from './client';
import {
  UserGroupDto,
  UserGroupCreateRequest,
  GroupMembershipDto,
  GroupInviteRequest,
  GroupDailyProgressDto,
} from '@/types/api.types';

export const groupsApi = {
  getAll: async (): Promise<UserGroupDto[]> => {
    const { data } = await apiClient.get<UserGroupDto[]>('/api/groups');
    return data;
  },

  getById: async (groupId: string): Promise<UserGroupDto> => {
    const { data } = await apiClient.get<UserGroupDto>(`/api/groups/${groupId}`);
    return data;
  },

  create: async (request: UserGroupCreateRequest): Promise<UserGroupDto> => {
    const { data } = await apiClient.post<UserGroupDto>('/api/groups', request);
    return data;
  },

  getMembers: async (groupId: string): Promise<GroupMembershipDto[]> => {
    const { data } = await apiClient.get<GroupMembershipDto[]>(`/api/groups/${groupId}/members`);
    return data;
  },

  inviteUsers: async (groupId: string, request: GroupInviteRequest): Promise<GroupMembershipDto[]> => {
    const { data } = await apiClient.post<GroupMembershipDto[]>(`/api/groups/${groupId}/invite`, request);
    return data;
  },

  getPendingInvitations: async (): Promise<GroupMembershipDto[]> => {
    const { data } = await apiClient.get<GroupMembershipDto[]>('/api/groups/invitations/pending');
    return data;
  },

  acceptInvitation: async (membershipId: string): Promise<GroupMembershipDto> => {
    const { data } = await apiClient.post<GroupMembershipDto>(`/api/groups/invitations/${membershipId}/accept`);
    return data;
  },

  rejectInvitation: async (membershipId: string): Promise<void> => {
    await apiClient.post(`/api/groups/invitations/${membershipId}/reject`);
  },

  leaveGroup: async (groupId: string): Promise<void> => {
    await apiClient.post(`/api/groups/${groupId}/leave`);
  },

  archiveGroup: async (groupId: string): Promise<void> => {
    await apiClient.post(`/api/groups/${groupId}/archive`);
  },

  getDailyProgress: async (groupId: string, date: string): Promise<GroupDailyProgressDto> => {
    const { data } = await apiClient.get<GroupDailyProgressDto>(`/api/groups/${groupId}/progress`, {
      params: { date },
    });
    return data;
  },

  getTodayProgress: async (groupId: string): Promise<GroupDailyProgressDto> => {
    const { data } = await apiClient.get<GroupDailyProgressDto>(`/api/groups/${groupId}/progress/today`);
    return data;
  },
};

