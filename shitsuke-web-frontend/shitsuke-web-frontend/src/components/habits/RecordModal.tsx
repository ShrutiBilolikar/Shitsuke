import { useState } from 'react';
import { Modal, ModalBody, ModalFooter } from '@/components/ui/Modal';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { RecordType, RecordCreateRequest } from '@/types/api.types';
import { format } from 'date-fns';

interface RecordModalProps {
  isOpen: boolean;
  onClose: () => void;
  habit: RecordType;
  selectedDate?: Date;
  onSubmit: (data: RecordCreateRequest) => void;
  isLoading?: boolean;
}

export const RecordModal = ({
  isOpen,
  onClose,
  habit,
  selectedDate,
  onSubmit,
  isLoading,
}: RecordModalProps) => {
  const [rawData, setRawData] = useState('');
  const dateStr = selectedDate ? format(selectedDate, 'yyyy-MM-dd') : format(new Date(), 'yyyy-MM-dd');

  const handleSubmit = () => {
    let value = rawData;

    // For Boolean type, use 'true' if they're logging it
    if (habit.type === 'Boolean') {
      value = 'true';
    }

    onSubmit({
      recordDate: dateStr,
      rawData: value || undefined,
    });

    setRawData('');
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Log ${habit.name}`} size="md">
      <ModalBody>
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Date
            </label>
            <input
              type="date"
              value={dateStr}
              disabled
              className="w-full px-4 py-2 rounded-lg border border-gray-300 bg-gray-50"
            />
          </div>

          {habit.type === 'Boolean' ? (
            <p className="text-sm text-gray-600">
              Click "Log Record" to mark this habit as completed for {format(selectedDate || new Date(), 'MMMM d, yyyy')}.
            </p>
          ) : habit.type === 'Number' ? (
            <Input
              label="Value"
              type="number"
              placeholder="Enter a number"
              value={rawData}
              onChange={(e) => setRawData(e.target.value)}
            />
          ) : (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Notes
              </label>
              <textarea
                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-charcoal"
                rows={4}
                placeholder="Enter your notes..."
                value={rawData}
                onChange={(e) => setRawData(e.target.value)}
              />
            </div>
          )}
        </div>
      </ModalBody>
      <ModalFooter>
        <Button onClick={onClose} variant="ghost">
          Cancel
        </Button>
        <Button onClick={handleSubmit} variant="primary" isLoading={isLoading}>
          Log Record
        </Button>
      </ModalFooter>
    </Modal>
  );
};
