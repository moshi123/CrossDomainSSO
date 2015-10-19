package com.nextyu.sso.server.service.impl;

import com.nextyu.sso.server.domain.Credential;
import com.nextyu.sso.server.domain.DemoLoginUser;
import com.nextyu.sso.server.domain.LoginUser;
import com.nextyu.sso.server.service.AuthenticationHandler;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author nextyu
 * @version 1.0
 */
@Service
public class DemoAuthenticationHandlerImpl implements AuthenticationHandler {
    @Override
    public LoginUser authenticate(Credential credential) {
        if ("admin".equals(credential.getParameter("name")) && "123".equals(credential.getParameter("password"))) {
            DemoLoginUser loginUser = new DemoLoginUser();
            loginUser.setLoginName("admin");
            return loginUser;
        }
        credential.setError("用户名或者密码错误");
        return null;
    }

    @Override
    public Set<String> authenticatedSystemIds(LoginUser loginUser) {
        return null;
    }
}
