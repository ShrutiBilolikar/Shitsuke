import { AppLayout } from '@/components/layout/AppLayout';
import { HabitCard } from '@/components/habits/HabitCard';
import { Button } from '@/components/ui/Button';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { EmptyState } from '@/components/ui/EmptyState';
import { useHabits } from '@/hooks/useHabits';
import { useNavigate } from 'react-router-dom';
import { PlusIcon, CheckCircleIcon } from '@heroicons/react/24/outline';

export const HabitsListPage = () => {
  const navigate = useNavigate();
  const { data: habits, isLoading } = useHabits();

  return (
    <AppLayout>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-charcoal">My Habits</h1>
            <p className="text-gray-600 mt-1">Track your daily disciplines</p>
          </div>
          <Button
            onClick={() => navigate('/app/habits/new')}
            variant="primary"
            className="flex items-center gap-2"
          >
            <PlusIcon className="h-5 w-5" />
            <span className="hidden sm:inline">New Habit</span>
          </Button>
        </div>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <LoadingSpinner size="lg" />
          </div>
        ) : !habits || habits.length === 0 ? (
          <EmptyState
            icon={<CheckCircleIcon className="h-16 w-16" />}
            title="No habits yet"
            description="Create your first habit to start building discipline"
            actionLabel="Create Habit"
            onAction={() => navigate('/app/habits/new')}
          />
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {habits.map((habit) => (
              <HabitCard key={habit.recordTypeId} habit={habit} />
            ))}
          </div>
        )}
      </div>
    </AppLayout>
  );
};
