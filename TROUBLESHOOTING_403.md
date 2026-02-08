# Troubleshooting 403 Errors on Friend Requests

## Problem
You're getting 403 Forbidden errors when trying to access `/api/friends` endpoints, even though you're logged in.

## Most Likely Cause
The JWT token contains your email, but when the backend tries to look up your user in the database, the user doesn't exist. This can happen if:
1. The database was reset/cleared
2. The user was deleted
3. You're using a token from before the database was reset

## How to Fix

### Step 1: Check Backend Console Logs
Look at your Spring Boot console output. You should see messages like:
```
JWT Filter: Extracted username: your-email@example.com for path: /api/friends
CustomUserDetailsService: Looking up user with email: your-email@example.com
CustomUserDetailsService: ✗ User not found in database: your-email@example.com
```

If you see "User not found", that's the problem.

### Step 2: Clear Your Browser Storage
1. Open Browser DevTools (F12)
2. Go to Application → Storage → Clear site data
3. Or manually delete:
   - `auth_token` from Local Storage
   - `auth-storage` from Local Storage

### Step 3: Register/Login Again
1. Go to the registration page
2. Register with your email (or use a different email if that one is taken)
3. Login with your credentials
4. Try sending a friend request again

### Step 4: Verify User Exists in Database
If you have database access, check if your user exists:
```sql
SELECT * FROM users WHERE email = 'your-email@example.com';
```

If the user doesn't exist, register again.

## Alternative: Check Token Validity

If the user exists but you still get 403, the token might be invalid:

1. Open Browser DevTools → Application → Local Storage
2. Copy the `auth_token` value
3. Decode it at https://jwt.io
4. Check if:
   - The email in the token matches your database email
   - The token hasn't expired (check `exp` field)

## Quick Test

Try this in your browser console:
```javascript
// Check if token exists
console.log('Token:', localStorage.getItem('auth_token'));

// Try to decode it (basic check)
const token = localStorage.getItem('auth_token');
if (token) {
  const payload = JSON.parse(atob(token.split('.')[1]));
  console.log('Token email:', payload.sub);
  console.log('Token expires:', new Date(payload.exp * 1000));
}
```

If the token email doesn't match any user in your database, you need to log in again.

