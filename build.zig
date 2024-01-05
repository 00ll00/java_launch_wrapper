const std = @import("std");
const Arch = std.Target.Cpu.Arch;

const NATIVE_VERSION = "1.4";

const TARGET_ARCH = [_]Arch{ .x86, .x86_64, .aarch64 };

pub fn build(b: *std.Build) void {
    const optimize = b.standardOptimizeOption(.{});

    const jui = b.addModule("jui", .{
        .source_file = .{ .path = "lib/jui/src/jui.zig" },
    });

    inline for (TARGET_ARCH) |arch| {
        const target = std.zig.CrossTarget{ .cpu_arch = arch, .os_tag = .windows, .abi = .none };
        const lib = b.addSharedLibrary(.{
            .name = "libjlw-" ++ @tagName(arch) ++ "-" ++ NATIVE_VERSION,
            .root_source_file = .{ .path = "src/main/zig/wrapper.zig" },
            .target = target,
            .optimize = optimize,
        });
        lib.addModule("jui", jui);
        b.installArtifact(lib);
    }
}
