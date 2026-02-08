import { format, startOfMonth, endOfMonth, eachDayOfInterval, getDay, isSameMonth, isSameDay, parseISO } from 'date-fns';
import { Record } from '@/types/api.types';

interface CalendarHeatmapProps {
  month: Date;
  records: Record[];
  onDateClick?: (date: Date) => void;
}

export const CalendarHeatmap = ({ month, records, onDateClick }: CalendarHeatmapProps) => {
  const monthStart = startOfMonth(month);
  const monthEnd = endOfMonth(month);
  const daysInMonth = eachDayOfInterval({ start: monthStart, end: monthEnd });

  // Create a map of dates to records for quick lookup
  const recordMap = new Map(
    records.map((record) => [
      format(parseISO(record.recordDate), 'yyyy-MM-dd'),
      record,
    ])
  );

  const getHeatmapColor = (date: Date) => {
    if (!isSameMonth(date, month)) return 'bg-gray-50';

    const dateKey = format(date, 'yyyy-MM-dd');
    const hasRecord = recordMap.has(dateKey);

    if (hasRecord) {
      return 'bg-heatmap-100 text-white';
    }

    return 'bg-gray-100';
  };

  const weeks: Date[][] = [];
  let currentWeek: Date[] = [];

  // Add empty cells for days before the month starts
  const firstDayOfWeek = getDay(monthStart);
  for (let i = 0; i < firstDayOfWeek; i++) {
    currentWeek.push(new Date(monthStart.getTime() - (firstDayOfWeek - i) * 86400000));
  }

  daysInMonth.forEach((day) => {
    currentWeek.push(day);
    if (getDay(day) === 6) {
      weeks.push(currentWeek);
      currentWeek = [];
    }
  });

  if (currentWeek.length > 0) {
    weeks.push(currentWeek);
  }

  return (
    <div className="w-full">
      <div className="grid grid-cols-7 gap-1 mb-2">
        {['S', 'M', 'T', 'W', 'T', 'F', 'S'].map((day, i) => (
          <div key={i} className="text-center text-xs font-medium text-gray-500">
            {day}
          </div>
        ))}
      </div>
      <div className="space-y-1">
        {weeks.map((week, weekIdx) => (
          <div key={weekIdx} className="grid grid-cols-7 gap-1">
            {week.map((day, dayIdx) => {
              const dateKey = format(day, 'yyyy-MM-dd');
              const record = recordMap.get(dateKey);
              const isCurrentMonth = isSameMonth(day, month);
              const isToday = isSameDay(day, new Date());

              return (
                <button
                  key={dayIdx}
                  onClick={() => onDateClick?.(day)}
                  disabled={!isCurrentMonth}
                  className={`
                    aspect-square flex items-center justify-center rounded-md text-sm font-medium
                    transition-all hover:scale-110
                    ${getHeatmapColor(day)}
                    ${isToday && isCurrentMonth ? 'ring-2 ring-gold ring-offset-1' : ''}
                    ${!isCurrentMonth ? 'opacity-30 cursor-not-allowed' : 'cursor-pointer'}
                  `}
                >
                  <span className={isCurrentMonth ? '' : 'text-gray-400'}>
                    {format(day, 'd')}
                  </span>
                </button>
              );
            })}
          </div>
        ))}
      </div>
      <div className="mt-4 flex items-center gap-4 text-xs text-gray-600">
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded bg-gray-100"></div>
          <span>No record</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded bg-heatmap-100"></div>
          <span>Logged</span>
        </div>
      </div>
    </div>
  );
};
