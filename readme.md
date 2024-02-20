# Java Launch Wrapper (v1.4.0)

## 有什么用？

如果你的 Windows 系统启用了 *Beta 版：使用Unicode UTF-8提供全球语言支持*，Java 可能会在读取命令行参数时使用错误的编码进行解码，导致一系列问题。

Bug 参考 [JDK-8272352](https://bugs.openjdk.org/browse/JDK-8272352)，已在 Java19 修复

使用此 Wrapper 可用于修复 **Class Path**, **-D 参数** 和 **App 参数** 中的乱码，解决大部分因此Bug无法运行的情况。

## 怎么用？

更改 Java 启动命令行，在 JVM 参数和主类之间插入`-jar java_launch_wrapper.jar`即可（需要 Java >= 1.6）。

例如：

> 原命令行：
> 
>     java -cp "路径1";"路径2" MainClass 参数1 参数2
> 
> 更改后：
> 
>     java -cp "路径1";"路径2" -jar "java_launch_wrapper.jar" MainClass 参数1 参数2

**注意：** 

1. 除`-jar`外原命令中的其他 jvm 选项可以直接保留，若原命令行中使用`-jar`则应该改为 ClassPath + MainClass 的形式。
2. Wrapper 会将动态链接库释放到系统临时目录，若系统的临时文件路径中也存在特殊字符，可以在 -jar 前添加 `-Doolloo.jlw.tmpdir="<自定义临时文件路径>"` 更改。
   在这种情况下需要保证此路径存在且无特殊字符。
3. 仅在 Windows 平台可用，因为这个 Bug 是 Windows 独家。

---

## 更新记录

### V1.4

- 支持 arm64 （未验证）
- 解析 `-D 参数` 并覆盖到 JVM 的 System.Properties
- 使用 zig 编译 x86_64 和 arm64 的 native 库，去除无关依赖，减小库体积（x86 库测试失败，使用旧版）
- 移除 dll 文件的 crc 校验
- 移除 `-Doolloo.jlw.silent` 选项，改为设置 `-Doolloo.jlw.debug=true` 启用 wrapper 调试信息

### V1.3

- 增加修改临时文件路径的启动参数
- 修复 dll 被占用导致无法启动多个进程的问题
- 增加 dll 文件校验
- 增加必要的调试信息输出，可设置 `-Doolloo.jlw.silent=true` 关闭

### V1.2

- 修复对std库的依赖问题
- 修复字符编码问题
- 将获取到的classpath写入jvm系统属性`java.class.path`以确保被包装应用能读取到正确的值

### V1.1

- 修复对 Java9 - Java15 的支持。
