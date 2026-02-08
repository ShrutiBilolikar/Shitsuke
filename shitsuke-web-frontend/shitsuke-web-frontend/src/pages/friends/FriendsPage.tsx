import { useState } from 'react';
import { AppLayout } from '@/components/layout/AppLayout';
import { Card, CardHeader, CardTitle, CardBody } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { EmptyState } from '@/components/ui/EmptyState';
import {
  useFriends,
  usePendingFriendRequests,
  useSentFriendRequests,
  useSendFriendRequest,
  useAcceptFriendRequest,
  useRejectFriendRequest,
  useRemoveFriend,
} from '@/hooks/useFriends';
import { useAuthStore } from '@/stores/authStore';
import { UsersIcon, UserPlusIcon, CheckIcon, XMarkIcon, TrashIcon } from '@heroicons/react/24/outline';

export const FriendsPage = () => {
  const { user } = useAuthStore();
  const [emailInput, setEmailInput] = useState('');

  const { data: friends, isLoading: friendsLoading } = useFriends();
  const { data: pending, isLoading: pendingLoading } = usePendingFriendRequests();
  const { data: sent, isLoading: sentLoading } = useSentFriendRequests();

  const sendRequest = useSendFriendRequest();
  const acceptRequest = useAcceptFriendRequest();
  const rejectRequest = useRejectFriendRequest();
  const removeFriend = useRemoveFriend();

  const handleSendRequest = () => {
    if (!emailInput.trim()) return;
    sendRequest.mutate(
      { recipientEmail: emailInput.trim() },
      {
        onSuccess: () => {
          setEmailInput('');
        },
      }
    );
  };

  const getFriendEmail = (friendship: any) => {
    // For accepted friendships, determine which email is the friend's
    if (friendship.userEmail === user?.email) {
      return friendship.friendEmail;
    } else {
      return friendship.userEmail;
    }
  };

  const getFriendName = (friendship: any) => {
    // For accepted friendships, determine which name is the friend's
    if (friendship.userEmail === user?.email) {
      return friendship.friendUsername || friendship.friendEmail;
    } else {
      return friendship.username || friendship.userEmail;
    }
  };

  return (
    <AppLayout>
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-charcoal">Friends</h1>
          <p className="text-gray-600 mt-1">Connect with friends for accountability</p>
        </div>

        {/* Send Friend Request */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Send Friend Request</CardTitle>
          </CardHeader>
          <CardBody>
            <div className="flex gap-2">
              <Input
                type="email"
                placeholder="Enter friend's email"
                value={emailInput}
                onChange={(e) => setEmailInput(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSendRequest()}
                className="flex-1"
              />
              <Button
                variant="primary"
                onClick={handleSendRequest}
                isLoading={sendRequest.isPending}
              >
                <UserPlusIcon className="h-5 w-5 mr-2" />
                Send Request
              </Button>
            </div>
          </CardBody>
        </Card>

        {/* Pending Friend Requests */}
        {pending && pending.length > 0 && (
          <Card className="mb-6">
            <CardHeader>
              <CardTitle>Pending Requests ({pending.length})</CardTitle>
            </CardHeader>
            <CardBody>
              <div className="space-y-3">
                {pending.map((request) => (
                  <div
                    key={request.friendshipId}
                    className="flex items-center justify-between p-3 rounded-lg bg-gray-50"
                  >
                    <div>
                      <div className="font-medium text-charcoal">
                        {request.userEmail === user?.email
                          ? request.friendUsername || request.friendEmail
                          : request.username || request.userEmail}
                      </div>
                      <div className="text-sm text-gray-600">
                        {request.userEmail === user?.email ? request.friendEmail : request.userEmail}
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <Button
                        variant="primary"
                        size="sm"
                        onClick={() => acceptRequest.mutate(request.friendshipId)}
                        isLoading={acceptRequest.isPending}
                      >
                        <CheckIcon className="h-4 w-4 mr-1" />
                        Accept
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => rejectRequest.mutate(request.friendshipId)}
                        isLoading={rejectRequest.isPending}
                      >
                        <XMarkIcon className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </CardBody>
          </Card>
        )}

        {/* Sent Friend Requests */}
        {sent && sent.length > 0 && (
          <Card className="mb-6">
            <CardHeader>
              <CardTitle>Sent Requests ({sent.length})</CardTitle>
            </CardHeader>
            <CardBody>
              <div className="space-y-3">
                {sent.map((request) => (
                  <div
                    key={request.friendshipId}
                    className="flex items-center justify-between p-3 rounded-lg bg-gray-50"
                  >
                    <div>
                      <div className="font-medium text-charcoal">
                        {request.friendUsername || request.friendEmail}
                      </div>
                      <div className="text-sm text-gray-600">{request.friendEmail}</div>
                    </div>
                    <span className="text-sm text-gray-500">Pending...</span>
                  </div>
                ))}
              </div>
            </CardBody>
          </Card>
        )}

        {/* Friends List */}
        <Card>
          <CardHeader>
            <CardTitle>My Friends ({friends?.length || 0})</CardTitle>
          </CardHeader>
          <CardBody>
            {friendsLoading ? (
              <div className="flex justify-center py-8">
                <LoadingSpinner />
              </div>
            ) : !friends || friends.length === 0 ? (
              <EmptyState
                icon={<UsersIcon className="h-16 w-16" />}
                title="No friends yet"
                description="Send friend requests to connect with others"
              />
            ) : (
              <div className="space-y-3">
                {friends.map((friendship) => {
                  const friendEmail = getFriendEmail(friendship);
                  const friendName = getFriendName(friendship);
                  return (
                    <div
                      key={friendship.friendshipId}
                      className="flex items-center justify-between p-3 rounded-lg bg-gray-50"
                    >
                      <div>
                        <div className="font-medium text-charcoal">{friendName}</div>
                        <div className="text-sm text-gray-600">{friendEmail}</div>
                      </div>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => {
                          if (confirm('Are you sure you want to remove this friend?')) {
                            removeFriend.mutate(friendship.friendshipId);
                          }
                        }}
                        isLoading={removeFriend.isPending}
                      >
                        <TrashIcon className="h-4 w-4" />
                      </Button>
                    </div>
                  );
                })}
              </div>
            )}
          </CardBody>
        </Card>
      </div>
    </AppLayout>
  );
};

