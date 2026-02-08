import { GroupMembershipDto } from '@/types/api.types';
import { Card, CardHeader, CardTitle, CardBody } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { useNavigate } from 'react-router-dom';
import { UserGroupIcon } from '@heroicons/react/24/outline';
import { format } from 'date-fns';

interface GroupInvitationCardProps {
  invitation: GroupMembershipDto;
  onAccept: (membershipId: string) => void;
  onReject: (membershipId: string) => void;
  isLoading?: boolean;
}

export const GroupInvitationCard = ({
  invitation,
  onAccept,
  onReject,
  isLoading = false,
}: GroupInvitationCardProps) => {
  const navigate = useNavigate();

  return (
    <Card>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-gold/20 flex items-center justify-center">
              <UserGroupIcon className="h-6 w-6 text-gold" />
            </div>
            <div>
              <CardTitle className="text-lg">{invitation.groupName}</CardTitle>
              <p className="text-sm text-gray-600 mt-1">
                Invited {invitation.invitedAt ? format(new Date(invitation.invitedAt), 'MMM d, yyyy') : ''}
              </p>
            </div>
          </div>
          <span className="text-xs px-2 py-1 rounded-full bg-yellow-100 text-yellow-700 font-medium">
            Pending
          </span>
        </div>
      </CardHeader>
      <CardBody>
        <div className="flex gap-2">
          <Button
            variant="primary"
            size="sm"
            onClick={() => onAccept(invitation.membershipId)}
            disabled={isLoading}
            className="flex-1"
          >
            Accept
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onReject(invitation.membershipId)}
            disabled={isLoading}
            className="flex-1"
          >
            Reject
          </Button>
        </div>
      </CardBody>
    </Card>
  );
};

