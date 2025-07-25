package com.xm.demotest.service.user;

import java.util.Map;

public interface RegisterService {
    Map<String, String> register(String username,
                                 String password,
                                 String phone,
                                 String name,
                                 String status);
}
