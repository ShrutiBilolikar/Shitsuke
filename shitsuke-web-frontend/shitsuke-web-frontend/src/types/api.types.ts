// Enums
export type RecordTypeEnum = 'Boolean' | 'Number' | 'Text';
export type CompletionRule = 'ALL_MEMBERS' | 'MAJORITY' | 'CUSTOM_PERCENTAGE';
export type GroupStatus = 'ACTIVE' | 'ARCHIVED';
export type MembershipStatus = 'INVITED' | 'ACTIVE' | 'LEFT';
export type MembershipRole = 'CREATOR' | 'MEMBER';
export type FriendshipStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

// User Types
export interface User {
  id: string;
  email: string;
  username?: string;
  createdAt: string;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest extends AuthRequest {
  username?: string;
}

export interface AuthResponse {
  token: string;
}

// RecordType Types
export interface RecordType {
  recordTypeId: string;
  name: string;
  type: RecordTypeEnum;
  user: User;
  isGroupMetric: boolean;
}

export interface RecordTypeRequest {
  name: string;
  type: RecordTypeEnum;
}

// Record Types
export interface Record {
  recordId: string;
  recordDate: string; // LocalDate as ISO string
  type: RecordTypeEnum;
  rawData?: string;
  recordTypeId: string;
  recordTypeName: string;
  userId: string;
  userEmail: string;
  username?: string;
}

export interface RecordRequest {
  recordTypeId: string;
  recordDate: string; // LocalDate as ISO string
  rawData?: string;
}

export interface RecordCreateRequest {
  recordDate: string; // LocalDate as ISO string
  rawData?: string;
}

// Streak Types
export interface StreakDto {
  recordTypeId: string;
  recordTypeName: string;
  currentStreak: number;
  longestStreak: number;
  streakStartDate?: string; // LocalDate as ISO string
  lastLoggedDate?: string; // LocalDate as ISO string
  isActive: boolean;
}

export interface GroupStreakDto {
  groupId: string;
  groupName: string;
  recordTypeId: string;
  recordTypeName: string;
  currentStreak: number;
  longestStreak: number;
  streakStartDate?: string; // LocalDate as ISO string
  lastCompletedDate?: string; // LocalDate as ISO string
  isActive: boolean;
}

// Group Types
export interface UserGroupDto {
  groupId: string;
  name: string;
  description?: string;
  creatorId: string;
  creatorEmail: string;
  creatorUsername?: string;
  recordTypeId: string;
  recordTypeName: string;
  completionRule: CompletionRule;
  customPercentage?: number;
  status: GroupStatus;
  activeMemberCount: number;
  createdAt: string; // Instant as ISO string
}

export interface UserGroupCreateRequest {
  name: string;
  description?: string;
  recordTypeId: string;
  completionRule: CompletionRule;
  customPercentage?: number;
}

export interface GroupInviteRequest {
  userEmails: string[];
}

export interface GroupMembershipDto {
  membershipId: string;
  groupId: string;
  groupName: string;
  userId: string;
  userEmail: string;
  username?: string;
  status: MembershipStatus;
  role: MembershipRole;
  invitedAt: string; // Instant as ISO string
  joinedAt?: string; // Instant as ISO string
  leftAt?: string; // Instant as ISO string
}

// Progress Types
export interface MemberProgressDto {
  userId: string;
  email: string;
  username?: string;
  hasLogged: boolean;
  recordValue?: string;
  role: MembershipRole;
}

export interface GroupDailyProgressDto {
  groupId: string;
  groupName: string;
  date: string; // LocalDate as ISO string
  totalMembers: number;
  membersWhoLogged: number;
  completionMet: boolean;
  members: MemberProgressDto[];
}

// Friendship Types
export interface FriendshipDto {
  friendshipId: string;
  userId: string;
  friendId: string;
  userEmail: string;
  friendEmail: string;
  username?: string;
  friendUsername?: string;
  status: FriendshipStatus;
  createdAt: string; // Instant as ISO string
  acceptedAt?: string; // Instant as ISO string
}

export interface FriendRequestDto {
  recipientEmail: string;
}

// API Error Response
export interface ApiError {
  message: string;
  status?: number;
}
