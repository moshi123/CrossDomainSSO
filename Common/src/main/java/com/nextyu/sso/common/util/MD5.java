package com.nextyu.sso.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密.
 *
 * @author nextyu
 * @version 1.0
 */
public class MD5 {
    private static final String[] HEX_DIGITS = new String[] { "0", "1", "2",
            "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    private MD5() {
    }

    public static String encode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text can't be null");
        }

        try {
            // md5编码
            byte[] source = text.getBytes("utf-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte[] dest = md.digest();

            // 结果转为hex string
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < dest.length; ++i) {
                builder.append(HEX_DIGITS[dest[i] >>> 4 & 0x0F]);
                builder.append(HEX_DIGITS[dest[i] & 0x0F]);
            }

            return builder.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(MD5.encode("test"));
    }
}
