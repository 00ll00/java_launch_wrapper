package oolloo.jlw;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathInjector {

    private static final int JAVA_VER;

    static {
        String ver = System.getProperty("java.specification.version");
        int pos = ver.indexOf('.');
        if (pos == -1) {
            JAVA_VER = Integer.parseInt(ver);
        } else {
            JAVA_VER = Integer.parseInt(ver.substring(pos + 1));
        }
    }

    private static URL transFilePathToURL(String filePath) throws MalformedURLException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        File file = new File(filePath);
        String url = file.toURI().toURL().toString().replace("!", "%21");
        return new URL(url);
    }

    public static void appendClassPath(String path) throws MalformedURLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        if (JAVA_VER <= 8) {
            appendClassPath8(path);
        } else {
            appendClassPath9(path);
        }
    }

    private static void appendClassPath8(String path) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        add.setAccessible(true);
        add.invoke(classLoader, transFilePathToURL(path));
    }

    private static void appendClassPath9(String path) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, MalformedURLException, InvocationTargetException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Class<?> clazz = classLoader.loadClass("jdk.internal.loader.BuiltinClassLoader");
        Class<?> ucpCls = classLoader.loadClass("jdk.internal.loader.URLClassPath");
        Field ucp = clazz.getDeclaredField("ucp");
        ucp.setAccessible(true);
        Method add = ucpCls.getDeclaredMethod("addURL", URL.class);
        add.setAccessible(true);
        add.invoke(ucp.get(classLoader), transFilePathToURL(path));
    }
}
