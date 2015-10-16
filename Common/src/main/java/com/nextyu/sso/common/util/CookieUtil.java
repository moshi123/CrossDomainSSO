package com.nextyu.sso.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author nextyu
 * @version 1.0
 */
public class CookieUtil {

    private CookieUtil() {
    }

    /**
     * 根据名称获取cookie.
     *
     * @param request
     * @param name
     * @return
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtil.isEmpty(name)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;

    }

    /**
     * 移除cookie.
     *
     * @param response
     * @param name
     * @param path
     * @param domain
     */
    public static void removeCookie(HttpServletResponse response, String name, String path, String domain) {
        Cookie cookie = new Cookie(name, null);
        if (!StringUtil.isEmpty(path)) {
            cookie.setPath(path);
        }
        if (!StringUtil.isEmpty(domain)) {
            cookie.setDomain(domain);
        }

        cookie.setMaxAge(-1000);
        response.addCookie(cookie);
    }
}
