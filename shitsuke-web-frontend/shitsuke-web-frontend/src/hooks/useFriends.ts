import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { friendsApi } from '@/services/api/friends.api';
import { FriendRequestDto } from '@/types/api.types';
import toast from 'react-hot-toast';

export const useFriends = () => {
  return useQuery({
    queryKey: ['friends'],
    queryFn: friendsApi.getAll,
  });
};

export const usePendingFriendRequests = () => {
  return useQuery({
    queryKey: ['pending-friend-requests'],
    queryFn: friendsApi.getPending,
  });
};

export const useSentFriendRequests = () => {
  return useQuery({
    queryKey: ['sent-friend-requests'],
    queryFn: friendsApi.getSent,
  });
};

export const useSendFriendRequest = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: FriendRequestDto) => friendsApi.sendFriendRequest(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sent-friend-requests'] });
      toast.success('Friend request sent!');
    },
    onError: (error: any) => {
      // Handle error message - could be in data.message or just data (string)
      const errorMessage = error.response?.data?.message || error.response?.data || 'Failed to send friend request';
      toast.error(errorMessage);
    },
  });
};

export const useAcceptFriendRequest = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (friendshipId: string) => friendsApi.acceptFriendRequest(friendshipId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pending-friend-requests'] });
      queryClient.invalidateQueries({ queryKey: ['friends'] });
      toast.success('Friend request accepted!');
    },
    onError: () => {
      toast.error('Failed to accept friend request');
    },
  });
};

export const useRejectFriendRequest = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (friendshipId: string) => friendsApi.rejectFriendRequest(friendshipId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pending-friend-requests'] });
      toast.success('Friend request rejected');
    },
    onError: () => {
      toast.error('Failed to reject friend request');
    },
  });
};

export const useRemoveFriend = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (friendshipId: string) => friendsApi.removeFriendship(friendshipId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['friends'] });
      toast.success('Friend removed');
    },
    onError: () => {
      toast.error('Failed to remove friend');
    },
  });
};

