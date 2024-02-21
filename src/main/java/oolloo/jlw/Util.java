package oolloo.jlw;

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


    static void debug(String msg) {
        if (DEBUG) System.out.println("jlw: " + msg);
    }


    static final boolean DEBUG = System.getProperty("oolloo.jlw.debug", "").equals("true");
}
