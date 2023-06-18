# Java Launch Wrapper (v1.3.3)

**用途：** 此 wrapper 使 java 能够正常添加启动命令行中包含特殊字符的 classpath 。

**使用方法：** 更改 java 启动命令行，例如：

> 原命令行：`java -cp "路径1";"路径2" MainClass 参数1 参数2`
> 
> 更改后：`java -cp "路径1";"路径2" -jar "java_launch_wrapper.jar" MainClass 参数1 参数2`

若系统的临时文件路径中也存在特殊字符，可以在 -jar 前添加 `-Doolloo.jlw.tmpdir="<自定义临时文件路径>"` 更改。
在这种情况下需要保证此路径存在。

**注意：** 

1. 除`-jar`外原命令中的其他 jvm 选项可以直接保留，若原命令行中使用`-jar`则应该改为 ClassPath + MainClass 的形式。
2. `-jar`选项一定放在所有 jvm 选项最后一项，否则会解析错误。
3. 仅在 Windows 平台可用。

---

## 更新记录

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