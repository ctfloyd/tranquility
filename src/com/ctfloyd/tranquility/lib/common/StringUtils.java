package com.ctfloyd.tranquility.lib.common;

public class StringUtils {

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    public static boolean isNotBlank(String str) { return !isBlank(str); }
    public static String parse(String escapedString)  {
        StringBuilder parsedString = new StringBuilder();
        for (int i = 0; i < escapedString.length(); i++) {
            char ch = escapedString.charAt(i);
            if (ch == '\\' && (i + 1 < escapedString.length())) {
                char nextCh = escapedString.charAt(i + 1);
                if (nextCh == 'n') {
                    parsedString.append('\n');
                    i += 1;
                }
            } else {
                parsedString.append(ch);
            }
        }
        return parsedString.toString();
    }

}
