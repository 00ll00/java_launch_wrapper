package oolloo.jlw;

import java.io.*;
import java.util.ArrayList;

import static oolloo.jlw.Util.DEBUG;
import static oolloo.jlw.Util.debug;

public class ArgLoader {

    private static native String getCommandLine();
    public static final String commandLine;
    public static final String[] args;

    private static void loadNative() throws Exception {

        String os_arch = System.getProperty("os.arch");
        String arch;

        if (os_arch.equals("x86") || os_arch.equals("i386")) {
            arch = "x86";
        } else if (os_arch.equals("x86_64") || os_arch.equals("amd64")) {
            arch = "x86_64";
        } else if (os_arch.equals("aarch64") || os_arch.equals("arm64")) {
            arch = "aarch64";
        } else {
            throw new Exception("unknown os.arch: " + os_arch);
        }

        String lib_name = "libjlw-" + arch + "-" + Wrapper.NATIVE_VERSION + ".dll";

        File tmp_dir = new File(System.getProperty("oolloo.jlw.tmpdir", System.getProperty("java.io.tmpdir", ".")));
        if (!tmp_dir.exists()) {
            tmp_dir = new File(".");
        }

        File lib = new File(tmp_dir, lib_name);

        if (lib.exists()) {
            debug(String.format("native file exists: '%s'.", lib.getAbsolutePath()));
            if (DEBUG) {
                debug("delete old native file.");
                if (!lib.delete()) throw new Exception();
            } else {
                try {
                    System.load(lib.getAbsolutePath());
                    return;  // existing file is ok
                } catch (UnsatisfiedLinkError ignored) {
                    debug(String.format("existing native file '%s' failed to load, trying to overwrite.", lib.getAbsolutePath()));
                }
            }
        }

        // release dll file
        debug(String.format("releasing native file to '%s'.", lib.getAbsolutePath()));

        InputStream is = ArgLoader.class.getResourceAsStream("/" + lib_name);
        assert is != null;

        FileOutputStream os = new FileOutputStream(lib);

        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            is.close();
            os.close();
        }

        System.load(lib.getAbsolutePath());
    }

    static {

        try {
            loadNative();
            debug("native file loaded.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        commandLine = getCommandLine();

        debug(String.format("got command line: %s", commandLine));

        int pos = 0;
        int length = commandLine.length();

        StringBuilder sb = new StringBuilder();

        char[] chars = commandLine.toCharArray();
        ArrayList<String> res = new ArrayList<String>();

        boolean inStr = false;

        while (pos < length) {
            char c = chars[pos++];
            switch (c) {
                case ' ':
                case '\t':
                    if (inStr) {
                        sb.append(c);
                    } else if (sb.length() > 0) {
                        res.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    break;
                case '\\':
                    if (pos < length && (chars[pos] == '"' || chars[pos] == '\\')) {
                        sb.append(chars[pos]);
                        pos ++;
                    } else {
                        sb.append(c);
                    }
                    break;
                case '"':
                    inStr = !inStr;
                    break;
                default:
                    sb.append(c);
            }
        }
        if (sb.length() > 0) {
            res.add(sb.toString());
        }
        args = res.toArray(new String[0]);
    }
}
