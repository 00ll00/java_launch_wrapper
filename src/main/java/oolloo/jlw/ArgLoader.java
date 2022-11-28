package oolloo.jlw;

import java.io.*;
import java.util.ArrayList;

public class ArgLoader {
    private final boolean IS_JVM_64;
    private native String getCommandLine();
    public final String commandLine;
    public final String[] args;

    ArgLoader() throws IOException {

        IS_JVM_64 = System.getProperty("sun.arch.data.model", System.getProperty("java.vm.name", "")).contains("64");

        loadNative();
        commandLine = getCommandLine();

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
        this.args = res.toArray(new String[0]);
    }

    private void loadNative() throws IOException {
        File tmp_dir = new File(System.getProperty("oolloo.jlw.tmpdir", System.getProperty("java.io.tmpdir")));

        InputStream is;
        File tmp;

        if (IS_JVM_64) {
            tmp = new File(tmp_dir,"libwrapper.dll");
            is = ArgLoader.class.getResourceAsStream("/wrapper.dll");
        } else {
            tmp = new File(tmp_dir, "libwrapper32.dll");
            is = ArgLoader.class.getResourceAsStream("/wrapper32.dll");
        }

        if (!tmp.exists()) {
            FileOutputStream os = new FileOutputStream(tmp);
            assert is != null;
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
        }

        System.load(tmp.getAbsolutePath());
    }
}
