package com.ctfloyd.tranquility;

import com.ctfloyd.tranquility.lib.test.JavaTestRunner;
import com.ctfloyd.tranquility.lib.test.JavascriptTestRunner;

public class TranquilityTest {

    private static final String TRANQUILITY_PACKAGE_PATH = "com.ctfloyd.tranquility";
    private static final String TRANQUILITY_TEST_JS_FILES_FOLDER = "test-js-files";

    public static void main(String[] args) {
        JavascriptTestRunner.run(TRANQUILITY_TEST_JS_FILES_FOLDER);
        JavaTestRunner.run(TRANQUILITY_PACKAGE_PATH);
    }
}
