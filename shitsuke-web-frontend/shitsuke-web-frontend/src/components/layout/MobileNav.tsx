import { NavLink } from 'react-router-dom';
import {
  HomeIcon,
  CheckCircleIcon,
  UserGroupIcon,
  UsersIcon,
  UserIcon,
} from '@heroicons/react/24/outline';

const navItems = [
  { to: '/app/dashboard', icon: HomeIcon, label: 'Home' },
  { to: '/app/habits', icon: CheckCircleIcon, label: 'Habits' },
  { to: '/app/groups', icon: UserGroupIcon, label: 'Groups' },
  { to: '/app/friends', icon: UsersIcon, label: 'Friends' },
  { to: '/app/profile', icon: UserIcon, label: 'Profile' },
];

export const MobileNav = () => {
  return (
    <nav className="lg:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 z-50">
      <div className="flex justify-around">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex flex-col items-center gap-1 py-3 px-4 min-w-0 flex-1 transition-colors ${
                isActive ? 'text-charcoal' : 'text-gray-500'
              }`
            }
          >
            <item.icon className="h-6 w-6" />
            <span className="text-xs font-medium">{item.label}</span>
          </NavLink>
        ))}
      </div>
    </nav>
  );
};
