package oolloo.jlw;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathInjector {

    private final int JAVA_VER;

    ClassPathInjector() {
        String ver = System.getProperty("java.specification.version");
        int pos = ver.indexOf('.');
        if (pos == -1) {
            JAVA_VER = Integer.parseInt(ver);
        } else {
            JAVA_VER = Integer.parseInt(ver.substring(pos + 1));
        }
    }

    public void appendClassPath(String path) throws MalformedURLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        if (JAVA_VER <= 8) {
            appendClassPath8(path);
        } else {
            appendClassPath9(path);
        }
    }

    private void appendClassPath8(String path) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        add.setAccessible(true);
        add.invoke(classLoader, new File(path).toURI().toURL());
    }

    private void appendClassPath9(String path) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, MalformedURLException, InvocationTargetException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Class<?> clazz = classLoader.loadClass("jdk.internal.loader.BuiltinClassLoader");
        Class<?> ucpCls = classLoader.loadClass("jdk.internal.loader.URLClassPath");
        Field ucp = clazz.getDeclaredField("ucp");
        ucp.setAccessible(true);
        Method add = ucpCls.getDeclaredMethod("addURL", URL.class);
        add.setAccessible(true);
        add.invoke(ucp.get(classLoader), new File(path).toURI().toURL());
    }
}
