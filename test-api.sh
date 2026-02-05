#!/bin/bash

# Shitsuke Streak Tracker API Testing Script
# Base URL
BASE_URL="http://localhost:8080"

echo "======================================"
echo "  SHITSUKE API TESTING SCRIPT"
echo "======================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counter
PASS=0
FAIL=0

# Function to print test result
test_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC}: $2"
        ((PASS++))
    else
        echo -e "${RED}✗ FAIL${NC}: $2"
        ((FAIL++))
    fi
    echo ""
}

# =====================================
# 1. AUTHENTICATION TESTS
# =====================================
echo -e "${BLUE}[1] AUTHENTICATION TESTS${NC}"
echo "--------------------------------------"

# Register User 1
echo "1.1 Register User 1 (alice@example.com)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123",
    "username": "alice"
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Register Alice"
echo "Response: $BODY"
echo ""

# Register User 2
echo "1.2 Register User 2 (bob@example.com)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bob@example.com",
    "password": "password123",
    "username": "bob"
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Register Bob"
echo ""

# Register User 3
echo "1.3 Register User 3 (charlie@example.com)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "charlie@example.com",
    "password": "password123",
    "username": "charlie"
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Register Charlie"
echo ""

# Login User 1
echo "1.4 Login as Alice"
RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }')
TOKEN_ALICE=$(echo $RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
test_result $([ ! -z "$TOKEN_ALICE" ] && echo 0 || echo 1) "Login Alice (Got token)"
echo "Alice Token: ${TOKEN_ALICE:0:50}..."
echo ""

# Login User 2
echo "1.5 Login as Bob"
RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bob@example.com",
    "password": "password123"
  }')
TOKEN_BOB=$(echo $RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
test_result $([ ! -z "$TOKEN_BOB" ] && echo 0 || echo 1) "Login Bob (Got token)"
echo ""

# Login User 3
echo "1.6 Login as Charlie"
RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "charlie@example.com",
    "password": "password123"
  }')
TOKEN_CHARLIE=$(echo $RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
test_result $([ ! -z "$TOKEN_CHARLIE" ] && echo 0 || echo 1) "Login Charlie (Got token)"
echo ""

# =====================================
# 2. FRIENDSHIP TESTS
# =====================================
echo -e "${BLUE}[2] FRIENDSHIP TESTS${NC}"
echo "--------------------------------------"

# Alice sends friend request to Bob
echo "2.1 Alice sends friend request to Bob"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/friends/request \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d '{
    "recipientEmail": "bob@example.com"
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
FRIENDSHIP_ID_1=$(echo $BODY | grep -o '"friendshipId":"[^"]*"' | cut -d'"' -f4)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Alice → Bob friend request"
echo "Friendship ID: $FRIENDSHIP_ID_1"
echo ""

# Alice sends friend request to Charlie
echo "2.2 Alice sends friend request to Charlie"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/friends/request \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d '{
    "recipientEmail": "charlie@example.com"
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
FRIENDSHIP_ID_2=$(echo $BODY | grep -o '"friendshipId":"[^"]*"' | cut -d'"' -f4)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Alice → Charlie friend request"
echo ""

# Bob accepts friend request
echo "2.3 Bob accepts Alice's friend request"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/friends/accept/$FRIENDSHIP_ID_1" \
  -H "Authorization: Bearer $TOKEN_BOB")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Bob accepts Alice's request"
echo ""

# Charlie accepts friend request
echo "2.4 Charlie accepts Alice's friend request"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/friends/accept/$FRIENDSHIP_ID_2" \
  -H "Authorization: Bearer $TOKEN_CHARLIE")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Charlie accepts Alice's request"
echo ""

# Bob sends friend request to Charlie
echo "2.5 Bob sends friend request to Charlie"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/friends/request \
  -H "Authorization: Bearer $TOKEN_BOB" \
  -H "Content-Type: application/json" \
  -d '{
    "recipientEmail": "charlie@example.com"
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
FRIENDSHIP_ID_3=$(echo $BODY | grep -o '"friendshipId":"[^"]*"' | cut -d'"' -f4)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Bob → Charlie friend request"
echo ""

# Charlie accepts
echo "2.6 Charlie accepts Bob's friend request"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/friends/accept/$FRIENDSHIP_ID_3" \
  -H "Authorization: Bearer $TOKEN_CHARLIE")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Charlie accepts Bob's request"
echo ""

# =====================================
# 3. RECORD TYPE TESTS
# =====================================
echo -e "${BLUE}[3] RECORD TYPE TESTS${NC}"
echo "--------------------------------------"

# Alice creates a Boolean record type (Workout)
echo "3.1 Alice creates Boolean record type: Daily Workout"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/record-types \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Daily Workout",
    "type": "Boolean"
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
RECORD_TYPE_WORKOUT=$(echo $BODY | grep -o '"recordTypeId":"[^"]*"' | cut -d'"' -f4)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Create Boolean record type"
echo "Record Type ID: $RECORD_TYPE_WORKOUT"
echo ""

# Alice creates a Number record type (Steps)
echo "3.2 Alice creates Number record type: Daily Steps"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/record-types \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Daily Steps",
    "type": "Number"
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
RECORD_TYPE_STEPS=$(echo $BODY | grep -o '"recordTypeId":"[^"]*"' | cut -d'"' -f4)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Create Number record type"
echo ""

# =====================================
# 4. GROUP TESTS (NEW!)
# =====================================
echo -e "${BLUE}[4] GROUP TESTS${NC}"
echo "--------------------------------------"

# Alice creates a group for Daily Workout
echo "4.1 Alice creates group: Workout Squad"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/groups \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Workout Squad\",
    \"description\": \"Let's workout together every day!\",
    \"recordTypeId\": \"$RECORD_TYPE_WORKOUT\",
    \"completionRule\": \"MAJORITY\",
    \"customPercentage\": null
  }")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
GROUP_ID=$(echo $BODY | grep -o '"groupId":"[^"]*"' | cut -d'"' -f4)
test_result $([ "$HTTP_CODE" = "201" ] && echo 0 || echo 1) "Create group"
echo "Group ID: $GROUP_ID"
echo "Response: $BODY"
echo ""

# Alice invites Bob and Charlie to the group
echo "4.2 Alice invites Bob and Charlie to group"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/groups/$GROUP_ID/invite" \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d '{
    "userEmails": ["bob@example.com", "charlie@example.com"]
  }')
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
MEMBERSHIP_ID_BOB=$(echo $BODY | grep -o '"membershipId":"[^"]*"' | head -1 | cut -d'"' -f4)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Invite users to group"
echo ""

# Bob views pending invitations
echo "4.3 Bob views pending invitations"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/groups/invitations/pending" \
  -H "Authorization: Bearer $TOKEN_BOB")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
MEMBERSHIP_ID_BOB_ACTUAL=$(echo $BODY | grep -o '"membershipId":"[^"]*"' | head -1 | cut -d'"' -f4)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "View pending invitations"
echo "Bob's Membership ID: $MEMBERSHIP_ID_BOB_ACTUAL"
echo ""

# Bob accepts invitation
echo "4.4 Bob accepts group invitation"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/groups/invitations/$MEMBERSHIP_ID_BOB_ACTUAL/accept" \
  -H "Authorization: Bearer $TOKEN_BOB")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Bob accepts invitation"
echo ""

# Charlie accepts invitation (get membership ID first)
echo "4.5 Charlie gets pending invitation"
RESPONSE=$(curl -s -X GET "$BASE_URL/api/groups/invitations/pending" \
  -H "Authorization: Bearer $TOKEN_CHARLIE")
MEMBERSHIP_ID_CHARLIE=$(echo $RESPONSE | grep -o '"membershipId":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Charlie's Membership ID: $MEMBERSHIP_ID_CHARLIE"

echo "4.6 Charlie accepts group invitation"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/groups/invitations/$MEMBERSHIP_ID_CHARLIE/accept" \
  -H "Authorization: Bearer $TOKEN_CHARLIE")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Charlie accepts invitation"
echo ""

# View group details
echo "4.7 Alice views group details"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/groups/$GROUP_ID" \
  -H "Authorization: Bearer $TOKEN_ALICE")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "View group details"
echo "Response: $BODY"
echo ""

# View group members
echo "4.8 Alice views group members"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/groups/$GROUP_ID/members" \
  -H "Authorization: Bearer $TOKEN_ALICE")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "View group members"
echo "Members: $BODY"
echo ""

# =====================================
# 5. RECORD TESTS
# =====================================
echo -e "${BLUE}[5] RECORD TESTS${NC}"
echo "--------------------------------------"

# Alice logs workout (Boolean)
echo "5.1 Alice logs workout for today"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/records \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d "{
    \"recordTypeId\": \"$RECORD_TYPE_WORKOUT\",
    \"recordDate\": \"$(date +%Y-%m-%d)\",
    \"rawData\": \"true\"
  }")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Alice logs workout"
echo ""

# Bob logs workout
echo "5.2 Bob logs workout for today"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/records \
  -H "Authorization: Bearer $TOKEN_BOB" \
  -H "Content-Type: application/json" \
  -d "{
    \"recordTypeId\": \"$RECORD_TYPE_WORKOUT\",
    \"recordDate\": \"$(date +%Y-%m-%d)\",
    \"rawData\": \"true\"
  }")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Bob logs workout"
echo ""

# Charlie logs workout
echo "5.3 Charlie logs workout for today"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/records \
  -H "Authorization: Bearer $TOKEN_CHARLIE" \
  -H "Content-Type: application/json" \
  -d "{
    \"recordTypeId\": \"$RECORD_TYPE_WORKOUT\",
    \"recordDate\": \"$(date +%Y-%m-%d)\",
    \"rawData\": \"true\"
  }")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Charlie logs workout"
echo ""

# Alice logs steps (Number)
echo "5.4 Alice logs steps for today"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/records \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d "{
    \"recordTypeId\": \"$RECORD_TYPE_STEPS\",
    \"recordDate\": \"$(date +%Y-%m-%d)\",
    \"rawData\": \"10500\"
  }")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "Alice logs 10,500 steps"
echo ""

# =====================================
# 6. GROUP PROGRESS TESTS
# =====================================
echo -e "${BLUE}[6] GROUP PROGRESS TESTS${NC}"
echo "--------------------------------------"

# View today's group progress
echo "6.1 Alice views today's group progress"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/groups/$GROUP_ID/progress/today" \
  -H "Authorization: Bearer $TOKEN_ALICE")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)
test_result $([ "$HTTP_CODE" = "200" ] && echo 0 || echo 1) "View today's group progress"
echo "Progress: $BODY"
echo ""

# =====================================
# SUMMARY
# =====================================
echo ""
echo "======================================"
echo "  TEST SUMMARY"
echo "======================================"
echo -e "${GREEN}PASSED: $PASS${NC}"
echo -e "${RED}FAILED: $FAIL${NC}"
TOTAL=$((PASS + FAIL))
echo "TOTAL:  $TOTAL"
echo ""

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}🎉 ALL TESTS PASSED!${NC}"
else
    echo -e "${RED}⚠️  Some tests failed. Check the output above.${NC}"
fi
echo ""
