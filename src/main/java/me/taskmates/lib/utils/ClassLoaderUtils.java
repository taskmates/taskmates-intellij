package me.taskmates.lib.utils;

public class ClassLoaderUtils {
    public static void withOverriddenContextClassLoader(Runnable runnable) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ClassLoaderUtils.class.getClassLoader());
            runnable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }
}
