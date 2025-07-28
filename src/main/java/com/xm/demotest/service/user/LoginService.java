package com.xm.demotest.service.user;

import java.util.Map;

public interface LoginService {
    Map<String, Object> login(String username, String password);
    Map<String, String> logout(String token);
    Map<String, Object> refreshToken(String token);
}
