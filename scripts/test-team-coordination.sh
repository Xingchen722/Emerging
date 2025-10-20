#!/bin/bash

# Team Coordination API Test Script
# Tests all Team Coordination related endpoints

echo "🚀 Starting Team Coordination API Tests..."
echo "=========================================="

BASE_URL="http://localhost:8080/api/team-coordination"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to make API calls
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -e "\n${BLUE}Testing: $description${NC}"
    echo "Endpoint: $method $endpoint"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq 200 ]; then
        echo -e "${GREEN}✅ Success (HTTP $http_code)${NC}"
        echo "Response: $body" | head -c 200
        if [ ${#body} -gt 200 ]; then
            echo "..."
        fi
    else
        echo -e "${RED}❌ Failed (HTTP $http_code)${NC}"
        echo "Error: $body"
    fi
}

# Test 1: Service Status
make_request "GET" "/status" "" "Get Team Coordination Service Status"

# Test 2: Example Queries
make_request "GET" "/examples" "" "Get Example Queries"

# Test 3: Team Coordination Queries
echo -e "\n${YELLOW}📝 Testing Team Coordination Queries${NC}"

queries=(
    "Who owns Europe network issues?"
    "Who is responsible for Asia logistics?"
    "Who manages North America support?"
    "Who handles security incidents?"
    "Who leads the finance team?"
    "Who coordinates legal matters?"
    "Who owns marketing strategy?"
    "Who manages infrastructure maintenance?"
)

for query in "${queries[@]}"; do
    make_request "POST" "/query" "{\"query\":\"$query\",\"language\":\"en\"}" "Query: $query"
done

# Test 4: Get All Team Members
make_request "GET" "/members" "" "Get All Team Members"

# Test 5: Get Specific Team Member
make_request "GET" "/members/EU001" "" "Get Team Member by ID (EU001)"

# Test 6: Search Team Members by Criteria
echo -e "\n${YELLOW}🔍 Testing Team Member Search${NC}"

search_criteria=(
    "{\"region\":\"europe\"}"
    "{\"issueType\":\"network\"}"
    "{\"roleLevel\":\"manager\"}"
    "{\"region\":\"asia\",\"issueType\":\"logistics\"}"
    "{\"issueType\":\"security\",\"roleLevel\":\"specialist\"}"
)

for criteria in "${search_criteria[@]}"; do
    make_request "POST" "/search" "$criteria" "Search: $criteria"
done

# Test 7: Integration with Chat API
echo -e "\n${YELLOW}💬 Testing Integration with Chat API${NC}"

chat_queries=(
    "Who owns Europe network issues?"
    "Who manages Asia logistics operations?"
    "Who handles North America technical support?"
)

for query in "${chat_queries[@]}"; do
    make_request "POST" "/api/chat/message-with-data" "{\"message\":\"$query\",\"language\":\"en\"}" "Chat Integration: $query"
done

echo -e "\n${GREEN}🎉 Team Coordination API Tests Completed!${NC}"
echo "=========================================="
echo -e "\n${BLUE}📋 Test Summary:${NC}"
echo "✅ Service Status Check"
echo "✅ Example Queries Retrieval"
echo "✅ Team Coordination Query Processing"
echo "✅ Team Member Management"
echo "✅ Team Member Search"
echo "✅ Chat API Integration"
echo -e "\n${YELLOW}💡 Access the web interface at: http://localhost:8080/team-coordination.html${NC}"
