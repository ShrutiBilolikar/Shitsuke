import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import { Card, CardHeader, CardTitle, CardBody } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { CalendarHeatmap } from '@/components/habits/CalendarHeatmap';
import { StreakDisplay } from '@/components/habits/StreakDisplay';
import { RecordModal } from '@/components/habits/RecordModal';
import { useHabits, useRecords, useStreak, useCreateRecord } from '@/hooks/useHabits';
import { ArrowLeftIcon, PlusIcon } from '@heroicons/react/24/outline';
import { RECORD_TYPE_COLORS } from '@/utils/constants';

export const HabitDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState<Date | undefined>();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentMonth, setCurrentMonth] = useState(new Date());

  const { data: habits, isLoading: habitsLoading } = useHabits();
  const { data: records, isLoading: recordsLoading } = useRecords(id || '');
  const { data: streak, isLoading: streakLoading } = useStreak(id || '');
  const createRecord = useCreateRecord();

  const habit = habits?.find((h) => h.recordTypeId === id);

  const handleDateClick = (date: Date) => {
    setSelectedDate(date);
    setIsModalOpen(true);
  };

  const handleLogToday = () => {
    setSelectedDate(new Date());
    setIsModalOpen(true);
  };

  const handleSubmit = (data: any) => {
    if (!id) return;
    createRecord.mutate(
      { recordTypeId: id, data },
      {
        onSuccess: () => {
          setIsModalOpen(false);
        },
      }
    );
  };

  if (habitsLoading || recordsLoading || streakLoading) {
    return (
      <AppLayout>
        <LoadingSpinner fullPage />
      </AppLayout>
    );
  }

  if (!habit) {
    return (
      <AppLayout>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <p>Habit not found</p>
        </div>
      </AppLayout>
    );
  }

  const color = RECORD_TYPE_COLORS[habit.type];

  return (
    <AppLayout>
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Button
          onClick={() => navigate('/app/habits')}
          variant="ghost"
          size="sm"
          className="mb-4"
        >
          <ArrowLeftIcon className="h-4 w-4 mr-2" />
          Back to Habits
        </Button>

        <div className="mb-8">
          <div className="flex items-center gap-3 mb-2">
            <div
              className="w-4 h-4 rounded-full"
              style={{ backgroundColor: color }}
            ></div>
            <h1 className="text-3xl font-bold text-charcoal">{habit.name}</h1>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-sm px-3 py-1 rounded-full bg-gray-100 text-gray-600">
              {habit.type}
            </span>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader>
              <CardTitle>Streak</CardTitle>
            </CardHeader>
            <CardBody>
              <StreakDisplay streak={streak} size="lg" />
            </CardBody>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Total Records</CardTitle>
            </CardHeader>
            <CardBody>
              <p className="text-4xl font-bold text-charcoal">{records?.length || 0}</p>
              <p className="text-sm text-gray-500 mt-1">entries logged</p>
            </CardBody>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Quick Log</CardTitle>
            </CardHeader>
            <CardBody>
              <Button
                onClick={handleLogToday}
                variant="primary"
                className="w-full flex items-center justify-center gap-2"
              >
                <PlusIcon className="h-5 w-5" />
                Log Today
              </Button>
            </CardBody>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle>Calendar</CardTitle>
              <div className="flex items-center gap-2">
                <Button
                  onClick={() => setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1))}
                  variant="ghost"
                  size="sm"
                >
                  ‹
                </Button>
                <span className="text-sm font-medium">
                  {currentMonth.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
                </span>
                <Button
                  onClick={() => setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1))}
                  variant="ghost"
                  size="sm"
                >
                  ›
                </Button>
              </div>
            </div>
          </CardHeader>
          <CardBody>
            <CalendarHeatmap
              month={currentMonth}
              records={records || []}
              onDateClick={handleDateClick}
            />
          </CardBody>
        </Card>

        {isModalOpen && (
          <RecordModal
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            habit={habit}
            selectedDate={selectedDate}
            onSubmit={handleSubmit}
            isLoading={createRecord.isPending}
          />
        )}
      </div>
    </AppLayout>
  );
};
