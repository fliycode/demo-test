#!/bin/bash

# 权限管理系统API测试脚本
# 使用方法: ./test_api.sh

BASE_URL="http://localhost:8080"
TOKEN=""

echo "=== 权限管理系统API测试 ==="
echo

# 1. 测试登录
echo "1. 测试用户登录..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/user/login" \
  -d "username=admin&password=123456")

echo "登录响应: $LOGIN_RESPONSE"

# 提取token
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ 登录失败，无法获取token"
    exit 1
fi

echo "✅ 登录成功，Token: $TOKEN"
echo

# 2. 测试获取用户权限
echo "2. 测试获取当前用户权限..."
PERMISSIONS_RESPONSE=$(curl -s -X GET "$BASE_URL/auth/permissions" \
  -H "Authorization: $TOKEN")

echo "权限响应: $PERMISSIONS_RESPONSE"
echo

# 3. 测试用户列表
echo "3. 测试获取用户列表..."
USER_LIST_RESPONSE=$(curl -s -X GET "$BASE_URL/user/list" \
  -H "Authorization: $TOKEN")

echo "用户列表响应: $USER_LIST_RESPONSE"
echo

# 4. 测试角色列表
echo "4. 测试获取角色列表..."
ROLE_LIST_RESPONSE=$(curl -s -X GET "$BASE_URL/role/list" \
  -H "Authorization: $TOKEN")

echo "角色列表响应: $ROLE_LIST_RESPONSE"
echo

# 5. 测试权限列表
echo "5. 测试获取权限列表..."
PERMISSION_LIST_RESPONSE=$(curl -s -X GET "$BASE_URL/permission/list" \
  -H "Authorization: $TOKEN")

echo "权限列表响应: $PERMISSION_LIST_RESPONSE"
echo

# 6. 测试添加用户
echo "6. 测试添加用户..."
ADD_USER_RESPONSE=$(curl -s -X POST "$BASE_URL/user/add" \
  -H "Authorization: $TOKEN" \
  -d "username=testuser&password=123456&name=测试用户&status=1")

echo "添加用户响应: $ADD_USER_RESPONSE"
echo

# 7. 测试分页查询用户
echo "7. 测试分页查询用户..."
USER_PAGE_RESPONSE=$(curl -s -X GET "$BASE_URL/user/page?pageNum=1&pageSize=5" \
  -H "Authorization: $TOKEN")

echo "分页用户响应: $USER_PAGE_RESPONSE"
echo

# 8. 测试权限检查
echo "8. 测试权限检查..."
PERMISSION_CHECK_RESPONSE=$(curl -s -X GET "$BASE_URL/auth/hasPermission?permission=user:list" \
  -H "Authorization: $TOKEN")

echo "权限检查响应: $PERMISSION_CHECK_RESPONSE"
echo

# 9. 测试无权限访问（应该返回403）
echo "9. 测试无权限访问..."
UNAUTHORIZED_RESPONSE=$(curl -s -X GET "$BASE_URL/user/list")

echo "无权限访问响应: $UNAUTHORIZED_RESPONSE"
echo

# 10. 测试用户注册
echo "10. 测试用户注册..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/user/register" \
  -d "username=newuser&password=123456&name=新用户")

echo "注册响应: $REGISTER_RESPONSE"
echo

echo "=== 测试完成 ==="

# 检查是否所有测试都成功
if [[ $LOGIN_RESPONSE == *"登录成功"* ]] && [[ $USER_LIST_RESPONSE == *"200"* ]]; then
    echo "✅ 主要功能测试通过"
else
    echo "❌ 部分测试失败，请检查系统状态"
fi