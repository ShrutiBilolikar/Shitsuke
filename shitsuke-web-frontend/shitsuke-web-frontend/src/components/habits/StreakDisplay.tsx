import { FireIcon } from '@heroicons/react/24/solid';
import { StreakDto } from '@/types/api.types';

interface StreakDisplayProps {
  streak?: StreakDto;
  size?: 'sm' | 'md' | 'lg';
}

export const StreakDisplay = ({ streak, size = 'md' }: StreakDisplayProps) => {
  if (!streak) {
    return (
      <div className="flex items-center gap-2 text-gray-400">
        <FireIcon className="h-5 w-5" />
        <span className="text-sm">0 day streak</span>
      </div>
    );
  }

  const sizeClasses = {
    sm: { icon: 'h-4 w-4', text: 'text-sm' },
    md: { icon: 'h-5 w-5', text: 'text-base' },
    lg: { icon: 'h-6 w-6', text: 'text-lg' },
  };

  const classes = sizeClasses[size];

  return (
    <div className="flex items-center gap-4">
      <div className={`flex items-center gap-2 ${streak.isActive ? 'text-sage' : 'text-gray-400'}`}>
        <FireIcon className={classes.icon} />
        <div>
          <p className={`${classes.text} font-bold`}>{streak.currentStreak}</p>
          <p className="text-xs text-gray-500">Current</p>
        </div>
      </div>
      <div className="text-gray-600">
        <p className={`${classes.text} font-bold`}>{streak.longestStreak}</p>
        <p className="text-xs text-gray-500">Best</p>
      </div>
    </div>
  );
};
