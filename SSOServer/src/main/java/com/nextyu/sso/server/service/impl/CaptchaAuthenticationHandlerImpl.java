package com.nextyu.sso.server.service.impl;

import com.nextyu.sso.common.util.MD5;
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
public class CaptchaAuthenticationHandlerImpl implements AuthenticationHandler {
    @Override
    public LoginUser authenticate(Credential credential) {

        // 获取session中保存的验证码
        String sessionCode = (String) credential.getSettedSessionValue();
        String captcha = credential.getParameter("captcha");

        if (!captcha.equalsIgnoreCase(sessionCode)) {
            credential.setError("验证码错误");
            return null;
        }

        String password = credential.getParameter("password");
        String encode = MD5.encode(MD5.encode("123") + sessionCode);

        if ("admin".equals(credential.getParameter("name")) && password.equals(encode)) {
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
