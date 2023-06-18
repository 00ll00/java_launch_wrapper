package oolloo.jlw;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.CRC32;

public class ArgLoader {
    @SuppressWarnings("FieldCanBeLocal") private final long CRC32_LIB32 = 0xB7F06136L;
    @SuppressWarnings("FieldCanBeLocal") private final long CRC32_LIB64 = 0x530B96F6L;
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
        File lib;

        if (IS_JVM_64) {
            lib = new File(tmp_dir,"libjlw-" + Wrapper.NATIVE_VERSION + ".dll");
            is = ArgLoader.class.getResourceAsStream("/wrapper.dll");
        } else {
            lib = new File(tmp_dir, "libjlw32-" + Wrapper.NATIVE_VERSION + ".dll");
            is = ArgLoader.class.getResourceAsStream("/wrapper32.dll");
        }

        if (lib.exists()) {
            Wrapper.log(String.format("native file exists: '%s'.", lib.getAbsolutePath()));
            // crc32 check
            boolean same = false;
            try {
                FileInputStream fs = new FileInputStream(lib);
                byte[] buffer = new byte[1024];
                int len;
                CRC32 crc32 = new CRC32();
                while ((len = fs.read(buffer)) != -1) crc32.update(buffer, 0, len);
                same = crc32.getValue() == (IS_JVM_64 ? CRC32_LIB64 : CRC32_LIB32);
            } catch (Exception ignored) {}

            if (same) {
                Wrapper.log("native file checked.");
                try {
                    System.load(lib.getAbsolutePath());
                    return;  // existing file is ok
                } catch (UnsatisfiedLinkError ignored) {
                    Wrapper.log(String.format("existing native file '%s' failed to load, trying to overwrite.", lib.getAbsolutePath()));
                }
            } else {
                Wrapper.log("existing native file check failed.");
            }
        }

        // release dll file
        Wrapper.log(String.format("releasing native file to '%s'.", lib.getAbsolutePath()));
        FileOutputStream os = new FileOutputStream(lib);
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

        System.load(lib.getAbsolutePath());
    }
}
