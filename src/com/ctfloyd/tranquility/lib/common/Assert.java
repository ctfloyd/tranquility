package com.ctfloyd.tranquility.lib.common;

public class Assert {

    public static void ASSERT(boolean expression) {
        ASSERT(expression, "");
    }

    public static void ASSERT(boolean expression, String message) {
        if (!expression) {
            throw new AssertionError("ASSERTION ERROR: " + message);
        }
    }

    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertNotReached(String message) {
        throw new AssertionError("ASSERT NOT REACHED: " + message);
    }

}
