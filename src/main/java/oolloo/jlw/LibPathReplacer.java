package oolloo.jlw;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static oolloo.jlw.Util.*;

public class LibPathReplacer {

    public static void replaceUsrPath(String path) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        if (JAVA_VER >= 15) {
            replaceUsrPath15(path);
        } else if (JAVA_VER >= 12) {
            replaceUsrPath12(path);
        } else {
            replaceUsrPath6(path);
        }
    }

    private static void replaceUsrPath6(String path) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        System.setProperty("java.library.path", path);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Field usr_paths = ClassLoader.class.getDeclaredField("usr_paths");
        usr_paths.setAccessible(true);
        Method initializePath = ClassLoader.class.getDeclaredMethod("initializePath", String.class);
        initializePath.setAccessible(true);
        Object paths = initializePath.invoke(null, path);
        usr_paths.set(classLoader, paths);
    }

    private static void replaceUsrPath12(String path) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        System.setProperty("java.library.path", path);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Field usr_paths = getDeclaredFieldForce(ClassLoader.class, "usr_paths");
        usr_paths.setAccessible(true);
        Method initializePath = ClassLoader.class.getDeclaredMethod("initializePath", String.class);
        initializePath.setAccessible(true);
        Object paths = initializePath.invoke(null, path);
        usr_paths.set(classLoader, paths);
    }

    private static void replaceUsrPath15(String path) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        System.setProperty("java.library.path", path);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Class<?> NativeLibraries = classLoader.loadClass("jdk.internal.loader.NativeLibraries");
        Class<?>[] clzs = NativeLibraries.getDeclaredClasses();
        Class<?> LibraryPaths = null;
        for (Class<?> c : clzs) {
            if (c.getSimpleName().equals("LibraryPaths")) {
                LibraryPaths = c;
            }
        }
        if (LibraryPaths == null) {
            throw new ClassNotFoundException("class LibraryPaths not exists in jdk.internal.loader.NativeLibraries");
        }
        Field usr_paths = LibraryPaths.getDeclaredField("USER_PATHS");
        usr_paths.setAccessible(true);
        removeFinal(usr_paths);
        Class<?> ClassLoaderHelper = classLoader.loadClass("jdk.internal.loader.ClassLoaderHelper");
        Method parsePath = ClassLoaderHelper.getDeclaredMethod("parsePath", String.class);
        parsePath.setAccessible(true);
        Object paths = parsePath.invoke(null, path);
        usr_paths.set(LibraryPaths, paths);
    }
}
