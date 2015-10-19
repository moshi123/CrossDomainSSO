package com.nextyu.sso.server.service.impl;

import com.nextyu.sso.server.domain.Credential;
import com.nextyu.sso.server.domain.DemoLoginUser;
import com.nextyu.sso.server.domain.LoginUser;
import com.nextyu.sso.server.service.AuthenticationHandler;
import org.springframework.stereotype.Service;

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


        if ("admin".equals(credential.getParameter("name")) && "123".equals(credential.getParameter("password"))) {
            DemoLoginUser loginUser = new DemoLoginUser();
            loginUser.setLoginName("admin");
            return loginUser;
        }
        credential.setError("用户名或者密码错误");
        return null;
    }
}