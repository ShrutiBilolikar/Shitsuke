import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

export const registerSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  username: z.string().optional(),
});

export const recordTypeSchema = z.object({
  name: z.string().min(1, 'Name is required').max(100, 'Name must be less than 100 characters'),
  type: z.enum(['Boolean', 'Number', 'Text']),
});

export const groupSchema = z.object({
  name: z.string().min(1, 'Name is required').max(100, 'Name must be less than 100 characters'),
  description: z.string().max(500, 'Description must be less than 500 characters').optional(),
  recordTypeId: z.string().uuid('Invalid record type'),
  completionRule: z.enum(['ALL_MEMBERS', 'MAJORITY', 'CUSTOM_PERCENTAGE']),
  customPercentage: z.number().min(1).max(100).optional(),
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;
export type RecordTypeFormData = z.infer<typeof recordTypeSchema>;
export type GroupFormData = z.infer<typeof groupSchema>;
