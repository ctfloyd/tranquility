package com.ctfloyd.tranquility.lib.common;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NumberUtils {

    private static final Set<Character> DIGITS = Stream.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').collect(Collectors.toSet());
    private static final Set<Character> OCTAL_DIGITS = Stream.of('0', '1', '2', '3', '4', '5', '6', '7').collect(Collectors.toSet());
    private static final Set<Character> HEXADECIMAL_DIGITS = Stream.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F').collect(Collectors.toSet());

    public static boolean isHexadecimal(String str) {
        if (StringUtils.isBlank(str) || str.length() <= 1) {
            return false;
        }

        boolean hasPrefix = str.charAt(0) == '0' && Character.toUpperCase(str.charAt(1)) == 'X';
        return hasPrefix && str.substring(2).chars()
                .mapToObj(i -> (char)i)
                .map(Character::toUpperCase)
                .allMatch(HEXADECIMAL_DIGITS::contains);
    }

    public static Long fromHexadecimal(String str) {
        return Long.parseLong(str.substring(2), 16);
    }

    public static boolean isOctal(String str) {
        if (StringUtils.isBlank(str) || str.length() <= 1) {
            return false;
        }

        boolean hasPrefix = str.charAt(0) == '0' && Character.toUpperCase(str.charAt(1)) == 'O';
        return hasPrefix && str.substring(2).chars()
                .mapToObj(i -> (char) i)
                .allMatch(OCTAL_DIGITS::contains);
    }

    public static Long fromOctal(String str) {
        return Long.parseLong(str.substring(2), 8);
    }

    public static boolean isDecimal(String str) {
        return str.chars().mapToObj(i -> (char) i).allMatch(DIGITS::contains);
    }

    public static Double fromDecimal(String str) {
        return Double.parseDouble(str);
    }

    public static boolean isParseable(String str) {
        return isHexadecimal(str) || isOctal(str) || isDecimal(str);
    }

    public static Number parse(String str) {
        if (isHexadecimal(str)) {
            return fromHexadecimal(str);
        }

        if (isOctal(str)) {
            return fromOctal(str);
        }

        if (isDecimal(str)) {
            return fromDecimal(str);
        }

        return null;
    }

}
