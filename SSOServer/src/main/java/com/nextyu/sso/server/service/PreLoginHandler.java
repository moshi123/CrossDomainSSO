package com.nextyu.sso.server.service;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 登录页前置处理器.
 *
 * @author nextyu
 * @version 1.0
 */
public interface PreLoginHandler {

    // 接口里面的成员变量默认就是public static final
    String SESSION_ATTR_NAME = "login_session_attr_name";

    /**
     * 前置处理.
     *
     * @param session
     * @return
     */
    Map<?, ?> handle(HttpSession session) throws Exception;

}
