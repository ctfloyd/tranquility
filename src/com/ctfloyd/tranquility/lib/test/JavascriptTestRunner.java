package com.ctfloyd.tranquility.lib.test;

import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Script;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class JavascriptTestRunner {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void run(String pathToFolderWithTestFiles) {
        File directory = new File(pathToFolderWithTestFiles);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory.");
        }

        processFilesInDirectory(directory);
    }

    private static void processFilesInDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                processFilesInDirectory(directory);
                return;
            } else {
                processFile(file);
            }
        }
    }

    private static void processFile(File file) {
        if (!file.getName().endsWith(".js")) {
            return;
        }

        if (!file.canRead()) {
            System.out.println("[WARN] Cannot read file: " + file.getName() + ". Skipping the test.");
            return;
        }

        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Path.of(file.getAbsolutePath()));
        } catch (Exception ex) {
            String message = "[WARN] An error occurred while reading lines for file (%s). %s";
            message = String.format(message, file.getName(), ex.getMessage());
            System.err.println(message);
            System.err.println(ex);
        }

        String expectedOutput = parseExpectedOutput(lines);
        String executableJavascriptCode = parseExecutableJavascriptCode(lines);
        evaluateJavascriptOutput(file.getName(), executableJavascriptCode, expectedOutput);
    }

    private static String parseExpectedOutput(List<String> lines) {
        StringBuilder expectedOutput = new StringBuilder(256);
        String firstLine = lines.get(0);
        boolean startOfOutput = false;
        for (int i = 0; i < firstLine.length(); i++) {
            char ch = firstLine.charAt(i);
            if (ch == '$') {
                startOfOutput = !startOfOutput;
            } else if (startOfOutput) {
                expectedOutput.append(ch);
            }
        }
        return expectedOutput.toString();
    }

    private static String parseExecutableJavascriptCode(List<String> lines) {
        List<String> code = lines.subList(1, lines.size());
        return String.join("\n", code);
    }

    private static void evaluateJavascriptOutput(String filename, String code, String expectedOutput) {
        PrintStream formerOutput = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(outputStream);
        System.setOut(output);

        try {
            Runtime runtime = new Runtime(true);
            Script script = Script.parseScript(code.toCharArray(), runtime.getRealm(), runtime);
            script.evaluate();
            System.setOut(formerOutput);

            String actualOutput = outputStream.toString()
                    .trim()
                    .replaceAll("\r", "")
                    .replaceAll("\n", "\\\\n");
            String message;
            if (!actualOutput.equals(expectedOutput)) {
                message = ANSI_RED + "[FAIL] %s - Expected: (%s), Actual: (%s)." + ANSI_RESET;
            } else {
                message = ANSI_GREEN + "[SUCCESS] %s - Expected: (%s), Actual: (%s)." + ANSI_RESET;
            }
            message = String.format(message, filename, expectedOutput, actualOutput);
            System.out.println(message);
        } finally {
            System.setOut(formerOutput);
        }
    }
}
