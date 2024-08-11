package oolloo.jlw;

import java.io.*;

public class NativeCommandLineLoader implements CommandLineLoader {

    private static native String getCommandLine();

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
            Wrapper.debug(String.format("native file exists: '%s'.", lib.getAbsolutePath()));
            if (Wrapper.DEBUG) {
                Wrapper.debug("delete old native file.");
                if (!lib.delete()) throw new Exception();
            } else {
                try {
                    System.load(lib.getAbsolutePath());
                    return;  // existing file is ok
                } catch (UnsatisfiedLinkError ignored) {
                    Wrapper.debug(String.format("existing native file '%s' failed to load, trying to overwrite.", lib.getAbsolutePath()));
                }
            }
        }

        // release dll file
        Wrapper.debug(String.format("releasing native file to '%s'.", lib.getAbsolutePath()));

        InputStream is = NativeCommandLineLoader.class.getResourceAsStream("/" + lib_name);
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

    @Override
    public String load() throws Exception {

        loadNative();
        Wrapper.debug("native file loaded.");

        return getCommandLine();
    }
}
