import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { groupSchema, GroupFormData } from '@/utils/validation.schemas';
import { useHabits } from '@/hooks/useHabits';

interface GroupFormProps {
  onSubmit: (data: GroupFormData) => void;
  isLoading?: boolean;
}

export const GroupForm = ({ onSubmit, isLoading }: GroupFormProps) => {
  const { data: habits } = useHabits();
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<GroupFormData>({
    resolver: zodResolver(groupSchema),
    defaultValues: {
      completionRule: 'MAJORITY',
    },
  });

  const completionRule = watch('completionRule');

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <Input
        label="Group Name"
        placeholder="e.g., Morning Exercise Group, Reading Club"
        error={errors.name?.message}
        {...register('name')}
      />

      <Input
        label="Description (Optional)"
        placeholder="Describe the group's purpose..."
        error={errors.description?.message}
        {...register('description')}
      />

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Habit to Track <span className="text-crimson">*</span>
        </label>
        <select
          {...register('recordTypeId')}
          className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-charcoal"
        >
          <option value="">Select a habit...</option>
          {habits?.map((habit) => (
            <option key={habit.recordTypeId} value={habit.recordTypeId}>
              {habit.name} ({habit.type})
            </option>
          ))}
        </select>
        {errors.recordTypeId && (
          <p className="mt-1 text-sm text-crimson">{errors.recordTypeId.message}</p>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Completion Rule <span className="text-crimson">*</span>
        </label>
        <select
          {...register('completionRule')}
          className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-charcoal"
        >
          <option value="ALL_MEMBERS">All Members Must Complete</option>
          <option value="MAJORITY">Majority Must Complete</option>
          <option value="CUSTOM_PERCENTAGE">Custom Percentage</option>
        </select>
        {errors.completionRule && (
          <p className="mt-1 text-sm text-crimson">{errors.completionRule.message}</p>
        )}
      </div>

      {completionRule === 'CUSTOM_PERCENTAGE' && (
        <Input
          type="number"
          label="Custom Percentage"
          placeholder="e.g., 75"
          min={1}
          max={100}
          error={errors.customPercentage?.message}
          {...register('customPercentage', { valueAsNumber: true })}
        />
      )}

      <Button type="submit" variant="primary" className="w-full" isLoading={isLoading}>
        Create Group
      </Button>
    </form>
  );
};

