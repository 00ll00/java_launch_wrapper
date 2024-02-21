package oolloo.jlw;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Util {

    static final int JAVA_VER;

    static {
        String ver = System.getProperty("java.specification.version");
        int pos = ver.indexOf('.');
        if (pos == -1) {
            JAVA_VER = Integer.parseInt(ver);
        } else {
            JAVA_VER = Integer.parseInt(ver.substring(pos + 1));
        }
    }


    static final boolean DEBUG = System.getProperty("oolloo.jlw.debug", "").equals("true");

    static void debug(String msg) {
        if (DEBUG) System.out.println("jlw: " + msg);
    }


    static Field getDeclaredFieldForce(Class<?> clazz, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
        getDeclaredFields0.setAccessible(true);
        Field[] fields = (Field[]) getDeclaredFields0.invoke(clazz, false);
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        throw new NoSuchFieldException(name);
    }

    static void removeFinal(Field field) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Field modifiers = getDeclaredFieldForce(Field.class, "modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }
}
