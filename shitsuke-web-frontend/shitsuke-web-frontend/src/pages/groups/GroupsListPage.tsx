import { AppLayout } from '@/components/layout/AppLayout';
import { Button } from '@/components/ui/Button';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { EmptyState } from '@/components/ui/EmptyState';
import { Card, CardHeader, CardTitle, CardBody } from '@/components/ui/Card';
import { useGroups, usePendingInvitations, useAcceptInvitation, useRejectInvitation } from '@/hooks/useGroups';
import { useNavigate } from 'react-router-dom';
import { PlusIcon, UserGroupIcon, EnvelopeIcon } from '@heroicons/react/24/outline';
import { format } from 'date-fns';
import { GroupInvitationCard } from '@/components/groups/GroupInvitationCard';

export const GroupsListPage = () => {
  const navigate = useNavigate();
  const { data: groups, isLoading } = useGroups();
  const { data: pendingInvitations, isLoading: invitationsLoading } = usePendingInvitations();
  const acceptInvitation = useAcceptInvitation();
  const rejectInvitation = useRejectInvitation();

  const handleAccept = (membershipId: string) => {
    acceptInvitation.mutate(membershipId, {
      onSuccess: () => {
        // Navigate to the group after accepting
        const invitation = pendingInvitations?.find((inv) => inv.membershipId === membershipId);
        if (invitation) {
          navigate(`/app/groups/${invitation.groupId}`);
        }
      },
    });
  };

  const handleReject = (membershipId: string) => {
    if (confirm('Are you sure you want to reject this invitation?')) {
      rejectInvitation.mutate(membershipId);
    }
  };

  return (
    <AppLayout>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-charcoal">My Groups</h1>
            <p className="text-gray-600 mt-1">Collaborate with friends on shared goals</p>
          </div>
          <Button
            onClick={() => navigate('/app/groups/new')}
            variant="primary"
            className="flex items-center gap-2"
          >
            <PlusIcon className="h-5 w-5" />
            <span className="hidden sm:inline">New Group</span>
          </Button>
        </div>

        {/* Pending Invitations Section */}
        {pendingInvitations && pendingInvitations.length > 0 && (
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4">
              <EnvelopeIcon className="h-5 w-5 text-gold" />
              <h2 className="text-xl font-semibold text-charcoal">Pending Invitations</h2>
              <span className="text-sm text-gray-600">({pendingInvitations.length})</span>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {pendingInvitations.map((invitation) => (
                <GroupInvitationCard
                  key={invitation.membershipId}
                  invitation={invitation}
                  onAccept={handleAccept}
                  onReject={handleReject}
                  isLoading={acceptInvitation.isPending || rejectInvitation.isPending}
                />
              ))}
            </div>
          </div>
        )}

        {/* My Groups Section */}
        {groups && groups.length > 0 && (
          <div className="mb-4">
            <h2 className="text-xl font-semibold text-charcoal mb-4">My Groups</h2>
          </div>
        )}

        {isLoading || invitationsLoading ? (
          <div className="flex justify-center py-12">
            <LoadingSpinner size="lg" />
          </div>
        ) : !groups || groups.length === 0 ? (
          pendingInvitations && pendingInvitations.length > 0 ? null : (
            <EmptyState
              icon={<UserGroupIcon className="h-16 w-16" />}
              title="No groups yet"
              description="Create a group to collaborate with friends on shared habits"
              actionLabel="Create Group"
              onAction={() => navigate('/app/groups/new')}
            />
          )
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {groups.map((group) => (
              <Card
                key={group.groupId}
                hover
                onClick={() => navigate(`/app/groups/${group.groupId}`)}
                className="cursor-pointer"
              >
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <CardTitle className="text-lg">{group.name}</CardTitle>
                    {group.status === 'ARCHIVED' && (
                      <span className="text-xs px-2 py-1 rounded-full bg-gray-200 text-gray-600">
                        Archived
                      </span>
                    )}
                  </div>
                </CardHeader>
                <CardBody>
                  {group.description && (
                    <p className="text-sm text-gray-600 mb-3 line-clamp-2">{group.description}</p>
                  )}
                  <div className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-gray-600">Habit:</span>
                      <span className="font-medium">{group.recordTypeName}</span>
                    </div>
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-gray-600">Members:</span>
                      <span className="font-medium">{group.activeMemberCount}</span>
                    </div>
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-gray-600">Rule:</span>
                      <span className="font-medium text-xs">
                        {group.completionRule === 'ALL_MEMBERS'
                          ? 'All Members'
                          : group.completionRule === 'MAJORITY'
                          ? 'Majority'
                          : `${group.customPercentage}%`}
                      </span>
                    </div>
                    <div className="text-xs text-gray-500 mt-3 pt-3 border-t">
                      Created {format(new Date(group.createdAt), 'MMM d, yyyy')}
                    </div>
                  </div>
                </CardBody>
              </Card>
            ))}
          </div>
        )}
      </div>
    </AppLayout>
  );
};
