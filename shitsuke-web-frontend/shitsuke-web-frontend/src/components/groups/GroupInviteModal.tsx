import { useState } from 'react';
import { Modal } from '@/components/ui/Modal';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { FriendshipDto, GroupMembershipDto } from '@/types/api.types';
import { useAuthStore } from '@/stores/authStore';

interface GroupInviteModalProps {
  isOpen: boolean;
  onClose: () => void;
  onInvite: (emails: string[]) => void;
  friends: FriendshipDto[];
  existingMembers: GroupMembershipDto[];
}

export const GroupInviteModal = ({
  isOpen,
  onClose,
  onInvite,
  friends,
  existingMembers,
}: GroupInviteModalProps) => {
  const [selectedEmails, setSelectedEmails] = useState<Set<string>>(new Set());
  const { user } = useAuthStore();

  // Helper function to get the friend's email (same logic as FriendsPage)
  const getFriendEmail = (friendship: FriendshipDto): string => {
    // If current user is the sender (userEmail), then friend is friendEmail
    // If current user is the recipient (friendEmail), then friend is userEmail
    if (friendship.userEmail === user?.email) {
      return friendship.friendEmail;
    } else {
      return friendship.userEmail;
    }
  };

  const availableFriends = friends.filter((friend) => {
    const friendEmail = getFriendEmail(friend);
    // Check if friend is already a member (ACTIVE or INVITED status)
    return !existingMembers.some((m) => m.userEmail === friendEmail && (m.status === 'ACTIVE' || m.status === 'INVITED'));
  });

  const toggleFriend = (email: string) => {
    const newSet = new Set(selectedEmails);
    if (newSet.has(email)) {
      newSet.delete(email);
    } else {
      newSet.add(email);
    }
    setSelectedEmails(newSet);
  };

  const handleInvite = () => {
    if (selectedEmails.size > 0) {
      onInvite(Array.from(selectedEmails));
      setSelectedEmails(new Set());
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Invite Friends to Group">
      <div className="space-y-4">
        {availableFriends.length === 0 ? (
          <p className="text-gray-600 text-center py-4">
            All your friends are already in this group or you have no friends yet.
          </p>
        ) : (
          <>
            <div className="max-h-64 overflow-y-auto space-y-2">
              {availableFriends.map((friend) => {
                const friendEmail = getFriendEmail(friend);
                // Get friend's name - if current user is sender, use friendUsername, else use username
                const friendName = friend.userEmail === user?.email 
                  ? (friend.friendUsername || friendEmail)
                  : (friend.username || friendEmail);
                return (
                  <label
                    key={friend.friendshipId}
                    className="flex items-center gap-3 p-3 rounded-lg border border-gray-200 cursor-pointer hover:bg-gray-50"
                  >
                    <input
                      type="checkbox"
                      checked={selectedEmails.has(friendEmail)}
                      onChange={() => toggleFriend(friendEmail)}
                      className="w-4 h-4 text-charcoal focus:ring-charcoal"
                    />
                    <div className="flex-1">
                      <div className="font-medium text-charcoal">{friendName}</div>
                      <div className="text-sm text-gray-600">{friendEmail}</div>
                    </div>
                  </label>
                );
              })}
            </div>
            <div className="flex justify-end gap-2 pt-4 border-t">
              <Button variant="ghost" onClick={onClose}>
                Cancel
              </Button>
              <Button
                variant="primary"
                onClick={handleInvite}
                disabled={selectedEmails.size === 0}
              >
                Invite {selectedEmails.size > 0 && `(${selectedEmails.size})`}
              </Button>
            </div>
          </>
        )}
      </div>
    </Modal>
  );
};

