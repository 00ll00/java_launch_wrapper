# Java Launch Wrapper (v1.1)

**用途：** 此 wrapper 使 java 能够正常添加启动命令行中包含特殊字符的 classpath 。

**使用方法：** 更改 java 启动命令行，例如：

> 原命令行：`java -cp "路径1";"路径2" MainClass 参数1 参数2`
> 
> 更改后：`java -cp "路径1";"路径2" -jar "java_launch_wrapper.jar" MainClass 参数1 参数2`

**注意：** 

1. 除`-jar`外原命令中的其他 jvm 选项可以直接保留，若原命令行中使用`-jar`则应该改为 ClassPath + MainClass 的形式。
2. `-jar`选项一定放在所有 jvm 选项最后一项，否则会解析错误。
3. 仅在 Windows 平台可用。

---

## 更新记录

### V1.1

- 修复对 Java9 - Java15 的支持。