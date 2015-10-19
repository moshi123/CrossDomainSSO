package com.nextyu.sso.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JSON工具类.
 *
 * @author nextyu
 * @version 1.0
 */
public class JSONUtil {
    public static void writeOut(HttpServletResponse response, String jsonString) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            response.getWriter().write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
