package com.nextyu.sso.server.service;

import com.nextyu.sso.server.domain.Credential;
import com.nextyu.sso.server.domain.LoginUser;

/**
 * 验证身份处理器.
 *
 * @author nextyu
 * @version 1.0
 */
public interface AuthenticationHandler {

    /**
     * 验证身份.
     *
     * @param credential
     * @return
     */
    LoginUser authenticate(Credential credential);
}
