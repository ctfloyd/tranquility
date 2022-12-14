package com.ctfloyd.tranquility.lib.common;

public class StringUtils {

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

}
