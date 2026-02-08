import { NavLink } from 'react-router-dom';
import {
  HomeIcon,
  CheckCircleIcon,
  UserGroupIcon,
  UsersIcon,
  UserIcon,
} from '@heroicons/react/24/outline';

const navItems = [
  { to: '/app/dashboard', icon: HomeIcon, label: 'Dashboard' },
  { to: '/app/habits', icon: CheckCircleIcon, label: 'Habits' },
  { to: '/app/groups', icon: UserGroupIcon, label: 'Groups' },
  { to: '/app/friends', icon: UsersIcon, label: 'Friends' },
  { to: '/app/profile', icon: UserIcon, label: 'Profile' },
];

export const Sidebar = () => {
  return (
    <aside className="hidden lg:flex lg:flex-col lg:w-64 bg-white border-r border-gray-200 h-[calc(100vh-4rem)]">
      <nav className="flex-1 px-4 py-6 space-y-2">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                isActive
                  ? 'bg-charcoal text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              }`
            }
          >
            <item.icon className="h-5 w-5" />
            <span className="font-medium">{item.label}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
};
