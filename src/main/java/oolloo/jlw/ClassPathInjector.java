package oolloo.jlw;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static oolloo.jlw.Util.JAVA_VER;

public class ClassPathInjector {

    public static void appendClassPath(String path) throws MalformedURLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        if (JAVA_VER >= 9) {
            appendClassPath9(path);
        } else {
            appendClassPath6(path);
        }
    }

    private static void appendClassPath6(String path) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        add.setAccessible(true);
        add.invoke(classLoader, new File(path).toURI().toURL());
    }

    private static void appendClassPath9(String path) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, MalformedURLException, InvocationTargetException {
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
