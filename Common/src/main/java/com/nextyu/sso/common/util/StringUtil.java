package com.nextyu.sso.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author nextyu
 * @version 1.0
 */
public class StringUtil {
    private StringUtil() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 向url后追加参数，拼接时需要判断连接符是? or &，同时需要对参数值进行编码.
     *
     * @param origURL
     * @param paramName
     * @param paramVal
     * @return
     */
    public static String appendURLParam(String origURL, String paramName, String paramVal) {
        if (isEmpty(origURL)) {
            return null;
        }
        String bound = origURL.contains("?") ? "&" : "?";
        try {
            return origURL + bound + paramName + "=" + URLEncoder.encode(paramVal, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }
}
