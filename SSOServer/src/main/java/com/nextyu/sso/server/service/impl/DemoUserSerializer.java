package com.nextyu.sso.server.service.impl;

import com.nextyu.sso.server.domain.DemoLoginUser;
import com.nextyu.sso.server.domain.LoginUser;
import com.nextyu.sso.server.service.UserSerializer;
import org.springframework.stereotype.Service;

/**
 * @author nextyu
 * @version 1.0
 */
@Service
public class DemoUserSerializer extends UserSerializer {
    @Override
    protected void translate(LoginUser loginUser, UserData userData) {
        // 实现类型已知，可强制转换
        DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;
        userData.setId(demoLoginUser.getLoginName());
        userData.setProperty("name", demoLoginUser.getLoginName());
        userData.setProperty("dept", "信息部");
        userData.setProperty("post", "IT管理员");
    }
}
