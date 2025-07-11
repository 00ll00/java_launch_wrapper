const std = @import("std");
const mem = std.mem;
const win = std.os.windows;
const jui = @import("jui");

const jstring = jui.jstring;
const jchar = jui.jchar;
const jobject = jui.jobject;
const JNIEnv = jui.JNIEnv;

const k32 = struct {
    pub extern "kernel32" fn GetCommandLineW() callconv(win.WINAPI) win.LPWSTR;
};

fn getCommandLine(jenv: *JNIEnv) !jstring {
    const cmd_line_w = k32.GetCommandLineW();
    const utf16le_slice = mem.sliceTo(cmd_line_w, 0);
    return try jenv.newString(utf16le_slice);
}

comptime {
    const javaGetCommandLine = struct {
        fn inner(jenv: *JNIEnv, _: jobject) callconv(jui.JNICALL) jstring {
            return jui.wrapErrors(getCommandLine, .{jenv});
        }
    }.inner;

    jui.exportAs("oolloo.jlw.NativeCommandLineLoader.getCommandLine", javaGetCommandLine);
}
