const std = @import("std");
const mem = std.mem;
const k32 = std.os.windows.kernel32;
const jui = @import("jui");

const jstring = jui.jstring;
const jchar = jui.jchar;
const jobject = jui.jobject;
const JNIEnv = jui.JNIEnv;

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
