#!/bin/bash

BASE_URL="http://localhost:8080/api"
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=== Testing Streak Calculation API ==="
echo ""

# Test counter
PASSED=0
FAILED=0

# Function to make API calls
call_api() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4

    if [ -n "$token" ]; then
        if [ -n "$data" ]; then
            curl -s -X "$method" "$BASE_URL$endpoint" \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer $token" \
                -d "$data"
        else
            curl -s -X "$method" "$BASE_URL$endpoint" \
                -H "Authorization: Bearer $token"
        fi
    else
        if [ -n "$data" ]; then
            curl -s -X "$method" "$BASE_URL$endpoint" \
                -H "Content-Type: application/json" \
                -d "$data"
        else
            curl -s -X "$method" "$BASE_URL$endpoint"
        fi
    fi
}

# Test function
test_endpoint() {
    local name=$1
    local method=$2
    local endpoint=$3
    local data=$4
    local token=$5
    local expected_status=$6

    echo -n "Testing: $name ... "
    response=$(call_api "$method" "$endpoint" "$data" "$token")

    if echo "$response" | grep -q "error\|Error\|exception\|Exception" && [ "$expected_status" != "error" ]; then
        echo -e "${RED}FAILED${NC}"
        echo "Response: $response"
        ((FAILED++))
    else
        echo -e "${GREEN}PASSED${NC}"
        echo "Response: $response"
        ((PASSED++))
    fi
    echo ""
}

# 1. Register user
echo "=== Step 1: Register User ==="
test_endpoint "Register user1" "POST" "/auth/register" \
    '{"email":"user1@test.com","password":"pass1","name":"User One"}' "" "success"

# 2. Login
echo "=== Step 2: Login ==="
LOGIN_RESPONSE=$(call_api "POST" "/auth/login" '{"email":"user1@test.com","password":"pass1"}' "")
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Token: $TOKEN"
echo ""

if [ -z "$TOKEN" ]; then
    echo -e "${RED}Failed to get token. Exiting.${NC}"
    exit 1
fi

# 3. Create a Record Type
echo "=== Step 3: Create Record Type ==="
RECORD_TYPE_RESPONSE=$(call_api "POST" "/recordtype" \
    '{"name":"Daily Exercise","dataType":"BOOLEAN"}' "$TOKEN")
RECORD_TYPE_ID=$(echo "$RECORD_TYPE_RESPONSE" | grep -o '"recordTypeId":"[^"]*"' | cut -d'"' -f4)
echo "Record Type ID: $RECORD_TYPE_ID"
echo ""

if [ -z "$RECORD_TYPE_ID" ]; then
    echo -e "${RED}Failed to create record type. Exiting.${NC}"
    exit 1
fi

# 4. Create records for a streak (7 consecutive days)
echo "=== Step 4: Create Records for Streak ==="
TODAY=$(date +%Y-%m-%d)

# Create records for the past 7 days (including today)
for i in {0..6}; do
    DATE=$(date -d "$TODAY -$i days" +%Y-%m-%d 2>/dev/null || date -v-${i}d -j -f "%Y-%m-%d" "$TODAY" +%Y-%m-%d)
    echo -n "Creating record for $DATE ... "
    RESPONSE=$(call_api "POST" "/records" \
        "{\"recordTypeId\":\"$RECORD_TYPE_ID\",\"recordDate\":\"$DATE\",\"rawData\":\"true\"}" "$TOKEN")
    if echo "$RESPONSE" | grep -q "recordId"; then
        echo -e "${GREEN}PASSED${NC}"
        ((PASSED++))
    else
        echo -e "${RED}FAILED${NC}"
        echo "Response: $RESPONSE"
        ((FAILED++))
    fi
done
echo ""

# 5. Test user streak endpoint
echo "=== Step 5: Test User Streak Calculation ==="
test_endpoint "Get user streak for record type" "GET" "/streaks/user/$RECORD_TYPE_ID" "" "$TOKEN" "success"

# 6. Create a group for group streak testing
echo "=== Step 6: Create Group and Test Group Streak ==="
GROUP_RESPONSE=$(call_api "POST" "/groups" \
    "{\"name\":\"Exercise Buddies\",\"recordTypeId\":\"$RECORD_TYPE_ID\",\"completionRule\":\"ALL_MEMBERS\"}" "$TOKEN")
GROUP_ID=$(echo "$GROUP_RESPONSE" | grep -o '"groupId":"[^"]*"' | cut -d'"' -f4)
echo "Group ID: $GROUP_ID"
echo ""

if [ -n "$GROUP_ID" ]; then
    test_endpoint "Get group streak" "GET" "/streaks/group/$GROUP_ID" "" "$TOKEN" "success"
fi

# Summary
echo ""
echo "================================"
echo -e "${GREEN}PASSED: $PASSED${NC}"
echo -e "${RED}FAILED: $FAILED${NC}"
echo "================================"
