import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, Link } from 'react-router-dom';
import { useState } from 'react';
import toast from 'react-hot-toast';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card } from '@/components/ui/Card';
import { registerSchema, RegisterFormData } from '@/utils/validation.schemas';
import { authApi } from '@/services/api/auth.api';
import { useAuthStore } from '@/stores/authStore';
import { queryClient } from '@/lib/queryClient';

export const RegisterPage = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const { setAuth } = useAuthStore();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true);
    try {
      const response = await authApi.register(data);

      // Create user object from registration data
      const user = {
        id: '', // Will be set from backend if needed
        email: data.email,
        username: data.username,
        createdAt: new Date().toISOString(),
      };

      // Clear any previous data from React Query cache
      queryClient.clear();
      // Invalidate all queries to force refetch
      queryClient.invalidateQueries();
      // Set new auth state
      setAuth(user, response.token);
      toast.success('Registration successful!');
      navigate('/app/dashboard');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-paper flex items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-charcoal mb-2">Shitsuke</h1>
          <p className="text-gray-600">Create your account to start tracking habits.</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Email"
            type="email"
            placeholder="your@email.com"
            error={errors.email?.message}
            {...register('email')}
          />

          <Input
            label="Password"
            type="password"
            placeholder="••••••••"
            helperText="Minimum 6 characters"
            error={errors.password?.message}
            {...register('password')}
          />

          <Input
            label="Username (Optional)"
            type="text"
            placeholder="your_username"
            helperText="Used for friend discovery"
            error={errors.username?.message}
            {...register('username')}
          />

          <Button
            type="submit"
            variant="primary"
            className="w-full"
            isLoading={isLoading}
          >
            Register
          </Button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-sm text-gray-600">
            Already have an account?{' '}
            <Link to="/login" className="text-gold hover:underline font-medium">
              Login here
            </Link>
          </p>
        </div>
      </Card>
    </div>
  );
};
