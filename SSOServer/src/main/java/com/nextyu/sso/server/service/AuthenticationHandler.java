package com.nextyu.sso.server.service;

import com.nextyu.sso.server.domain.Credential;
import com.nextyu.sso.server.domain.LoginUser;

import java.util.Set;

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

    /**
     * 获取当前登录用户可用系统id列表.
     *
     * @param loginUser 登录用户
     * @return 返回null表示全部
     */
    Set<String> authenticatedSystemIds(LoginUser loginUser);
}
