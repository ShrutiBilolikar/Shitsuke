import { AppLayout } from '@/components/layout/AppLayout';
import { Card } from '@/components/ui/Card';
import { HabitForm } from '@/components/habits/HabitForm';
import { useCreateHabit } from '@/hooks/useHabits';
import { useNavigate } from 'react-router-dom';
import { RecordTypeFormData } from '@/utils/validation.schemas';

export const CreateHabitPage = () => {
  const navigate = useNavigate();
  const createHabit = useCreateHabit();

  const handleSubmit = (data: RecordTypeFormData) => {
    createHabit.mutate(data, {
      onSuccess: () => {
        navigate('/app/habits');
      },
    });
  };

  return (
    <AppLayout>
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-charcoal">Create New Habit</h1>
          <p className="text-gray-600 mt-1">
            Define a habit you want to track daily
          </p>
        </div>

        <Card>
          <HabitForm onSubmit={handleSubmit} isLoading={createHabit.isPending} />
        </Card>
      </div>
    </AppLayout>
  );
};
