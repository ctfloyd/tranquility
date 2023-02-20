package com.ctfloyd.tranquility.lib.test;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class JavaTestRunner {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final String TEST_MESSAGE = "%sTEST SUITE: [%25.25s] (%5.1f%%): %4d / %4d%s";

    public static void run(String basePackage) {
        try {
            List<String> messages = getClasses(basePackage).stream()
                    .filter(clazz -> clazz.getAnnotationsByType(Suite.class).length > 0)
                    .map(JavaTestRunner::runSuite)
                    .collect(Collectors.toList());
            messages.forEach(System.out::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String runSuite(Class<?> clazz) {
        int success = 0;
        Method[] classMethods = clazz.getDeclaredMethods();
        List<Method> testMethods = Arrays.stream(classMethods)
                .filter(method -> method.isAnnotationPresent(Test.class))
                .collect(Collectors.toList());
        for (Method method : testMethods) {
            try {
                method.invoke(clazz.getDeclaredConstructor().newInstance());
                success++;
            } catch (Exception ex) {
                System.err.println("[ERROR IN " + clazz.getSimpleName() + " - " + method.getName() + "]");
                ex.getCause().printStackTrace();
            }
        }
        int size = testMethods.size();
        float percentage = ((float) success / (float) size) * 100;
        boolean allPassed = success == size;
        if (allPassed) {
            return String.format(TEST_MESSAGE, ANSI_GREEN, clazz.getSimpleName(), percentage, success, size, ANSI_RESET);
        } else {
            return String.format(TEST_MESSAGE, ANSI_RED, clazz.getSimpleName(), percentage, success, size, ANSI_RESET);
        }
    }

    private static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }

}
