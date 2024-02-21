package oolloo.jlw;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.lang.System.arraycopy;
import static oolloo.jlw.Util.DEBUG;
import static oolloo.jlw.Util.debug;

public class Wrapper {

    static final String NATIVE_VERSION = "1.4.0";

    public static void main(String[] ignore) throws Exception {

        String[] args = ArgLoader.args;
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
                if (flag.startsWith("-D")) {
                    String key = flag.substring(2);
                    if (key.equals("java.library.path")) {
                        LibPathReplacer.replaceUsrPath(arg);
                    }
                    System.setProperty(key, arg);
                } else if ("-cp".equals(flag) || "--classpath".equals(flag) || "--class-path".equals(flag)) {
                    System.setProperty("java.class.path", arg);
                    for (String path : arg.split(File.pathSeparator)) ClassPathInjector.appendClassPath(path);
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

        if (DEBUG) {
            debug(String.format("Main Class: %s", mainClass));
            debug(String.format("App Arguments: %s", Arrays.toString(args)));
            debug("System Properties:");

            Set<Map.Entry<Object, Object>> s =  System.getProperties().entrySet();

            for (Map.Entry<Object, Object> e : s) {
                debug(String.format("  %s", e));
            }
        }

        Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(mainClass);
        Method main = clazz.getDeclaredMethod("main", String[].class);
        main.setAccessible(true);
        main.invoke(null, (Object) args);
    }
}
