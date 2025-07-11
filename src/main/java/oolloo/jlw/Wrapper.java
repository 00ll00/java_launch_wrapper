package oolloo.jlw;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.lang.System.arraycopy;

public class Wrapper {

    static final String NATIVE_VERSION = "1.4.4";

    static final boolean DEBUG = System.getProperty("oolloo.jlw.debug", "").equals("true");

    public static void main(String[] originArgs) throws Throwable {

        if (DEBUG) {
            debug("=====  Origin  =====");
            debug(String.format("App Arguments: %s", Arrays.toString(originArgs)));
            debug("System Properties:");

            Set<Map.Entry<Object, Object>> s =  System.getProperties().entrySet();

            for (Map.Entry<Object, Object> e : s) {
                debug(String.format("  %s", e));
            }

            debug("====================");
        }

        String commandLine;

        try {
            commandLine = new NativeCommandLineLoader().load();
        } catch (Throwable e1) {
            // Additionally captures UnsatisfiedLinkError.
            if (e1 instanceof Error && !(e1 instanceof UnsatisfiedLinkError)) throw e1;
            debug("native command line loader failed with exception:");
            debug(e1.getMessage());
            debug("try cim command line loader.");
            try {
                commandLine = new CimCommandLineLoader().load();
            } catch (Exception e2) {
                debug("cim command line loader failed with exception:");
                debug(e2.getMessage());
                throw new Exception("All CommandLine Loaders Failed.");
            }
        }

        debug(String.format("got command line: %s", commandLine));

        String[] args = ArgParser.parse(commandLine);

        debug(String.format("got raw args: %s", Arrays.toString(args)));

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
                    System.setProperty(flag.substring(2), arg);
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

        if (DEBUG) {
            debug("===== Injected =====");
            debug(String.format("Main Class: %s", clazzMain));
            debug(String.format("App Arguments: %s", Arrays.toString(argsOut)));
            debug("System Properties:");

            Set<Map.Entry<Object, Object>> s =  System.getProperties().entrySet();

            for (Map.Entry<Object, Object> e : s) {
                debug(String.format("  %s", e));
            }

            debug("====================");
        }

        invokeMain(clazzMain, argsOut);
    }

    private static void invokeMain(String mainClass, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(mainClass);
        Method main = clazz.getDeclaredMethod("main", String[].class);
        main.setAccessible(true);
        main.invoke(null, (Object) args);
    }

    static void debug(String msg) {
        if (DEBUG) System.out.println("jlw: " + msg);
    }
}
