package com.xm.demotest.service.user;

import java.util.Map;

public interface LoginService {
    Map<String, String> getToken(String username, String password);
}
