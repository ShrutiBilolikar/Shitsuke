import { AppLayout } from '@/components/layout/AppLayout';
import { Card } from '@/components/ui/Card';
import { GroupForm } from '@/components/groups/GroupForm';
import { useCreateGroup } from '@/hooks/useGroups';
import { useNavigate } from 'react-router-dom';
import { GroupFormData } from '@/utils/validation.schemas';

export const CreateGroupPage = () => {
  const navigate = useNavigate();
  const createGroup = useCreateGroup();

  const handleSubmit = (data: GroupFormData) => {
    createGroup.mutate(data, {
      onSuccess: (group) => {
        navigate(`/app/groups/${group.groupId}`);
      },
    });
  };

  return (
    <AppLayout>
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-charcoal">Create New Group</h1>
          <p className="text-gray-600 mt-1">
            Create a group to collaborate with friends on shared habits
          </p>
        </div>

        <Card>
          <div className="p-6">
            <GroupForm onSubmit={handleSubmit} isLoading={createGroup.isPending} />
          </div>
        </Card>
      </div>
    </AppLayout>
  );
};

