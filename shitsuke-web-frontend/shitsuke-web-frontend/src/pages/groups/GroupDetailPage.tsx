import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import { Card, CardHeader, CardTitle, CardBody } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import {
  useGroup,
  useGroupMembers,
  useGroupProgress,
  useGroupStreak,
  useLeaveGroup,
  useArchiveGroup,
  useInviteToGroup,
} from '@/hooks/useGroups';
import { useAuthStore } from '@/stores/authStore';
import { ArrowLeftIcon, UserGroupIcon, UserIcon } from '@heroicons/react/24/outline';
import { format } from 'date-fns';
import { GroupInviteModal } from '@/components/groups/GroupInviteModal';
import { useFriends } from '@/hooks/useFriends';

export const GroupDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false);

  const { data: group, isLoading: groupLoading } = useGroup(id || '');
  const { data: members, isLoading: membersLoading } = useGroupMembers(id || '');
  const { data: progress, isLoading: progressLoading } = useGroupProgress(id || '');
  const { data: streak, isLoading: streakLoading } = useGroupStreak(id || '');
  const { data: friends } = useFriends();
  const leaveGroup = useLeaveGroup();
  const archiveGroup = useArchiveGroup();
  const inviteToGroup = useInviteToGroup();

  const isCreator = group?.creatorEmail === user?.email;
  const currentMember = members?.find((m) => m.userEmail === user?.email);

  const handleInvite = (emails: string[]) => {
    if (!id) return;
    inviteToGroup.mutate(
      { groupId: id, request: { userEmails: emails } },
      {
        onSuccess: () => {
          setIsInviteModalOpen(false);
        },
      }
    );
  };

  const handleLeave = () => {
    if (!id || !confirm('Are you sure you want to leave this group?')) return;
    leaveGroup.mutate(id, {
      onSuccess: () => {
        navigate('/app/groups');
      },
    });
  };

  const handleArchive = () => {
    if (!id || !confirm('Are you sure you want to archive this group? This cannot be undone.')) return;
    archiveGroup.mutate(id, {
      onSuccess: () => {
        navigate('/app/groups');
      },
    });
  };

  if (groupLoading || membersLoading) {
    return (
      <AppLayout>
        <LoadingSpinner fullPage />
      </AppLayout>
    );
  }

  if (!group) {
    return (
      <AppLayout>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <p>Group not found</p>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout>
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Button
          onClick={() => navigate('/app/groups')}
          variant="ghost"
          size="sm"
          className="mb-4"
        >
          <ArrowLeftIcon className="h-4 w-4 mr-2" />
          Back to Groups
        </Button>

        <div className="mb-8">
          <div className="flex items-start justify-between mb-4">
            <div>
              <h1 className="text-3xl font-bold text-charcoal">{group.name}</h1>
              {group.description && (
                <p className="text-gray-600 mt-2">{group.description}</p>
              )}
            </div>
            {isCreator && (
              <Button variant="ghost" size="sm" onClick={handleArchive}>
                Archive Group
              </Button>
            )}
            {!isCreator && currentMember && (
              <Button variant="ghost" size="sm" onClick={handleLeave}>
                Leave Group
              </Button>
            )}
          </div>

          <div className="flex flex-wrap gap-4 text-sm">
            <div>
              <span className="text-gray-600">Habit:</span>{' '}
              <span className="font-medium">{group.recordTypeName}</span>
            </div>
            <div>
              <span className="text-gray-600">Members:</span>{' '}
              <span className="font-medium">{group.activeMemberCount}</span>
            </div>
            <div>
              <span className="text-gray-600">Rule:</span>{' '}
              <span className="font-medium">
                {group.completionRule === 'ALL_MEMBERS'
                  ? 'All Members'
                  : group.completionRule === 'MAJORITY'
                  ? 'Majority'
                  : `${group.customPercentage}%`}
              </span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {streak && (
            <Card>
              <CardHeader>
                <CardTitle>Group Streak</CardTitle>
              </CardHeader>
              <CardBody>
                <div className="text-3xl font-bold text-charcoal mb-2">
                  {streak.currentStreak} days
                </div>
                <p className="text-sm text-gray-600">
                  Longest: {streak.longestStreak} days
                </p>
              </CardBody>
            </Card>
          )}

          {progress && (
            <Card>
              <CardHeader>
                <CardTitle>Today's Progress</CardTitle>
              </CardHeader>
              <CardBody>
                <div className="text-3xl font-bold text-charcoal mb-2">
                  {progress.membersWhoLogged} / {progress.totalMembers}
                </div>
                <p className="text-sm text-gray-600">
                  {progress.completionMet ? '✓ Goal met!' : 'Goal not met yet'}
                </p>
              </CardBody>
            </Card>
          )}
        </div>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle>Members</CardTitle>
              {currentMember && (
                <Button
                  variant="primary"
                  size="sm"
                  onClick={() => setIsInviteModalOpen(true)}
                >
                  Invite Friends
                </Button>
              )}
            </div>
          </CardHeader>
          <CardBody>
            {membersLoading ? (
              <LoadingSpinner />
            ) : members && members.length > 0 ? (
              <div className="space-y-3">
                {members.map((member) => (
                  <div
                    key={member.membershipId}
                    className="flex items-center justify-between p-3 rounded-lg bg-gray-50"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-charcoal/10 flex items-center justify-center">
                        <UserIcon className="h-5 w-5 text-charcoal" />
                      </div>
                      <div>
                        <div className="font-medium text-charcoal">
                          {member.username || member.userEmail}
                        </div>
                        <div className="text-sm text-gray-600">{member.userEmail}</div>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      {member.role === 'CREATOR' && (
                        <span className="text-xs px-2 py-1 rounded-full bg-gold/20 text-gold font-medium">
                          Creator
                        </span>
                      )}
                      <span
                        className={`text-xs px-2 py-1 rounded-full ${
                          member.status === 'ACTIVE'
                            ? 'bg-green-100 text-green-700'
                            : member.status === 'INVITED'
                            ? 'bg-yellow-100 text-yellow-700'
                            : 'bg-gray-100 text-gray-600'
                        }`}
                      >
                        {member.status}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-600 text-center py-4">No members yet</p>
            )}
          </CardBody>
        </Card>

        {progress && progress.members && progress.members.length > 0 && (
          <Card className="mt-6">
            <CardHeader>
              <CardTitle>Today's Member Progress</CardTitle>
            </CardHeader>
            <CardBody>
              <div className="space-y-2">
                {progress.members.map((member) => (
                  <div
                    key={member.userId}
                    className="flex items-center justify-between p-2 rounded"
                  >
                    <span className="text-sm">
                      {member.username || member.email}
                    </span>
                    <span
                      className={`text-sm font-medium ${
                        member.hasLogged ? 'text-green-600' : 'text-gray-400'
                      }`}
                    >
                      {member.hasLogged ? '✓ Logged' : 'Not logged'}
                    </span>
                  </div>
                ))}
              </div>
            </CardBody>
          </Card>
        )}

        {isInviteModalOpen && friends && (
          <GroupInviteModal
            isOpen={isInviteModalOpen}
            onClose={() => setIsInviteModalOpen(false)}
            onInvite={handleInvite}
            friends={friends}
            existingMembers={members || []}
          />
        )}
      </div>
    </AppLayout>
  );
};

