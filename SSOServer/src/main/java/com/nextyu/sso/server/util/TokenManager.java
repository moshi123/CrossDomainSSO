package com.nextyu.sso.server.util;

import com.nextyu.sso.server.domain.LoginUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储VT_USER信息.
 *
 * @author nextyu
 * @version 1.0
 */
public class TokenManager {
    private static Map<String, LoginUser> map = new HashMap<>();

    private TokenManager() {

    }

    /**
     * 验证vt有效性.
     *
     * @param vt
     * @return
     */
    public static LoginUser validate(String vt) {
        return map.get(vt);
    }
}
