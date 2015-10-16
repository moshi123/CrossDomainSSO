package com.nextyu.sso.common.util;

import java.util.UUID;

/**
 * @author nextyu
 * @version 1.0
 */
public class KeyGenerator {
    private KeyGenerator() {
    }

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
