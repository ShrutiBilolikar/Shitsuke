import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { recordTypeSchema, RecordTypeFormData } from '@/utils/validation.schemas';

interface HabitFormProps {
  onSubmit: (data: RecordTypeFormData) => void;
  isLoading?: boolean;
}

export const HabitForm = ({ onSubmit, isLoading }: HabitFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RecordTypeFormData>({
    resolver: zodResolver(recordTypeSchema),
    defaultValues: {
      type: 'Boolean',
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <Input
        label="Habit Name"
        placeholder="e.g., Morning Exercise, Read for 30 min"
        error={errors.name?.message}
        {...register('name')}
      />

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Type
        </label>
        <select
          {...register('type')}
          className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-charcoal"
        >
          <option value="Boolean">Boolean (Yes/No)</option>
          <option value="Number">Number (e.g., steps, pages)</option>
          <option value="Text">Text (notes, thoughts)</option>
        </select>
        {errors.type && (
          <p className="mt-1 text-sm text-crimson">{errors.type.message}</p>
        )}
      </div>

      <Button type="submit" variant="primary" className="w-full" isLoading={isLoading}>
        Create Habit
      </Button>
    </form>
  );
};
