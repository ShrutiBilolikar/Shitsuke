import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import { Card, CardHeader, CardTitle, CardBody } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { EmptyState } from '@/components/ui/EmptyState';
import { HabitCard } from '@/components/habits/HabitCard';
import { RecordModal } from '@/components/habits/RecordModal';
import { useHabits, useCreateRecord } from '@/hooks/useHabits';
import { useGroups, usePendingInvitations } from '@/hooks/useGroups';
import { useFriends } from '@/hooks/useFriends';
import { RecordType } from '@/types/api.types';
import {
  CheckCircleIcon,
  UserGroupIcon,
  UsersIcon,
  PlusIcon,
} from '@heroicons/react/24/outline';

export const DashboardPage = () => {
  const navigate = useNavigate();
  const { data: habits, isLoading } = useHabits();
  const { data: groups } = useGroups();
  const { data: friends } = useFriends();
  const { data: pendingInvitations } = usePendingInvitations();
  const createRecord = useCreateRecord();
  const [selectedHabit, setSelectedHabit] = useState<RecordType | null>(null);

  const handleQuickLog = (habit: RecordType) => {
    setSelectedHabit(habit);
  };

  const handleSubmit = (data: any) => {
    if (!selectedHabit) return;
    createRecord.mutate(
      { recordTypeId: selectedHabit.recordTypeId, data },
      {
        onSuccess: () => {
          setSelectedHabit(null);
        },
      }
    );
  };

  return (
    <AppLayout>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-charcoal">Dashboard</h1>
          <p className="text-gray-600 mt-1">Track your daily progress</p>
        </div>

        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardBody>
              <div className="flex items-center gap-4">
                <div className="p-3 bg-sage/10 rounded-lg">
                  <CheckCircleIcon className="h-8 w-8 text-sage" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-charcoal">
                    {habits?.length || 0}
                  </p>
                  <p className="text-sm text-gray-600">Active Habits</p>
                </div>
              </div>
            </CardBody>
          </Card>

          <Card
            hover
            onClick={() => navigate('/app/groups')}
            className="cursor-pointer"
          >
            <CardBody>
              <div className="flex items-center gap-4">
                <div className="p-3 bg-gold/10 rounded-lg">
                  <UserGroupIcon className="h-8 w-8 text-gold" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-charcoal">{groups?.length || 0}</p>
                  <p className="text-sm text-gray-600">Groups</p>
                </div>
              </div>
            </CardBody>
          </Card>

          <Card
            hover
            onClick={() => navigate('/app/friends')}
            className="cursor-pointer"
          >
            <CardBody>
              <div className="flex items-center gap-4">
                <div className="p-3 bg-blue-100 rounded-lg">
                  <UsersIcon className="h-8 w-8 text-blue-600" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-charcoal">{friends?.length || 0}</p>
                  <p className="text-sm text-gray-600">Friends</p>
                </div>
              </div>
            </CardBody>
          </Card>
        </div>

        {/* My Habits Section */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle>My Habits</CardTitle>
              <Button
                onClick={() => navigate('/app/habits')}
                variant="ghost"
                size="sm"
              >
                View All
              </Button>
            </div>
          </CardHeader>
          <CardBody>
            {isLoading ? (
              <div className="flex justify-center py-8">
                <LoadingSpinner />
              </div>
            ) : !habits || habits.length === 0 ? (
              <EmptyState
                icon={<CheckCircleIcon className="h-12 w-12" />}
                title="No habits yet"
                description="Create your first habit to start tracking"
                actionLabel="Create Habit"
                onAction={() => navigate('/app/habits/new')}
              />
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {habits.slice(0, 6).map((habit) => (
                  <div key={habit.recordTypeId} className="relative">
                    <HabitCard habit={habit} />
                    <Button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleQuickLog(habit);
                      }}
                      variant="secondary"
                      size="sm"
                      className="absolute top-4 right-4"
                    >
                      <PlusIcon className="h-4 w-4" />
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </CardBody>
        </Card>

        {/* Groups and Friends Sections */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-8">
          <Card
            hover
            onClick={() => navigate('/app/groups')}
            className="cursor-pointer"
          >
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle>Groups</CardTitle>
                {pendingInvitations && pendingInvitations.length > 0 && (
                  <span className="flex items-center justify-center w-6 h-6 rounded-full bg-gold text-white text-xs font-bold">
                    {pendingInvitations.length}
                  </span>
                )}
              </div>
            </CardHeader>
            <CardBody>
              <p className="text-gray-600 text-sm mb-4">
                Collaborate with friends on shared goals
                {pendingInvitations && pendingInvitations.length > 0 && (
                  <span className="block mt-2 text-gold font-medium">
                    {pendingInvitations.length} pending invitation{pendingInvitations.length > 1 ? 's' : ''}
                  </span>
                )}
              </p>
              <Button
                variant="primary"
                size="sm"
                onClick={(e) => {
                  e.stopPropagation();
                  navigate('/app/groups');
                }}
              >
                View Groups
              </Button>
            </CardBody>
          </Card>

          <Card
            hover
            onClick={() => navigate('/app/friends')}
            className="cursor-pointer"
          >
            <CardHeader>
              <CardTitle>Friends</CardTitle>
            </CardHeader>
            <CardBody>
              <p className="text-gray-600 text-sm mb-4">
                Connect with friends for accountability
              </p>
              <Button
                variant="primary"
                size="sm"
                onClick={(e) => {
                  e.stopPropagation();
                  navigate('/app/friends');
                }}
              >
                View Friends
              </Button>
            </CardBody>
          </Card>
        </div>

        {selectedHabit && (
          <RecordModal
            isOpen={!!selectedHabit}
            onClose={() => setSelectedHabit(null)}
            habit={selectedHabit}
            onSubmit={handleSubmit}
            isLoading={createRecord.isPending}
          />
        )}
      </div>
    </AppLayout>
  );
};
