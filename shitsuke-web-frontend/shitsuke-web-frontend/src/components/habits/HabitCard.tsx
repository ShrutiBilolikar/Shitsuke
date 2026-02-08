import { Card } from '@/components/ui/Card';
import { RecordType } from '@/types/api.types';
import { RECORD_TYPE_COLORS } from '@/utils/constants';
import { useNavigate } from 'react-router-dom';

interface HabitCardProps {
  habit: RecordType;
}

export const HabitCard = ({ habit }: HabitCardProps) => {
  const navigate = useNavigate();
  const color = RECORD_TYPE_COLORS[habit.type];

  return (
    <Card
      hover
      onClick={() => navigate(`/app/habits/${habit.recordTypeId}`)}
      className="cursor-pointer"
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-2">
            <div
              className="w-3 h-3 rounded-full"
              style={{ backgroundColor: color }}
            ></div>
            <h3 className="font-semibold text-charcoal">{habit.name}</h3>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-600">
              {habit.type}
            </span>
            {habit.isGroupMetric && (
              <span className="text-xs px-2 py-1 rounded-full bg-gold/20 text-gold font-medium">
                Group
              </span>
            )}
          </div>
        </div>
      </div>
    </Card>
  );
};
