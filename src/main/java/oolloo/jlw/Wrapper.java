package oolloo.jlw;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.System.arraycopy;

public class Wrapper {

    public static void main(String[] ignore)
        throws InvocationTargetException,
            NoSuchMethodException,
            IllegalAccessException,
            ClassNotFoundException,
            IOException,
            NoSuchFieldException {

        ClassPathInjector injector = new ClassPathInjector();

        String[] args = new ArgLoader().args;
        int pos = 1;
        final int len = args.length;
        String clazzMain = null;
        String[] argsOut = null;
        do {
            String flag = args[pos++];
            String arg = "";
            if (flag.charAt(0) == '-') {
                int eqPos = flag.indexOf('=');
                if (eqPos > -1) {
                    arg = flag.substring(eqPos + 1);
                    flag = flag.substring(0, eqPos);
                } else if (args[pos].charAt(0) != '-') {
                    arg = args[pos];
                }
                if ("-cp".equals(flag) || "--classpath".equals(flag) || "--class-path".equals(flag)) {
                    for (String path : arg.split(";")) injector.appendClassPath(path);
                } else if ("-jar".equals(flag)) {
                    pos++;
                    clazzMain = args[pos++];
                    int lenOut = len - pos;
                    argsOut = new String[lenOut];
                    arraycopy(args, pos, argsOut, 0, lenOut);
                    pos = len;
                }
            }
        } while (pos < len);
        invokeMain(clazzMain, argsOut);
    }

    private static void invokeMain(String mainClass, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(mainClass);
        Method main = clazz.getDeclaredMethod("main", String[].class);
        main.setAccessible(true);
        main.invoke(clazz, (Object) args);
    }
}
