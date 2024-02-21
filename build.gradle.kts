plugins {
    id("java")
}

group = "oolloo"
version = "1.4.1"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}

tasks.register<Exec>("zigBuild") {
    mustRunAfter("clearLibs")
    commandLine("zig12", "build", "-Doptimize=ReleaseFast")
}

tasks.register<Copy>("copyLibs") {
    mustRunAfter("zigBuild")
    from("zig-out/lib")
    include("**/*.dll")
    into("src/main/resources")

    dependsOn("copyOldLib32")
}

// this task should be removed when new x86 lib available
tasks.register<Copy>("copyOldLib32") {
    mustRunAfter("zigBuild")
    from("lib/legacy")
    include("**/*.dll")
    into("src/main/resources")
    rename("wrapper32.dll","libjlw-x86-$version.dll")
}

tasks.findByPath(":processResources")?.mustRunAfter("copyLibs")

tasks.register<Delete>("clearLibs") {
    delete("zig-out/lib", "src/main/resources")
}

tasks.findByPath(":clean")?.dependsOn("clearLibs")

tasks.jar {

    dependsOn("clearLibs", "zigBuild", "copyLibs")

    manifest {
        attributes ( mapOf (
            "Main-Class" to "oolloo.jlw.Wrapper",
            "Add-Opens" to "java.base/jdk.internal.loader java.base/java.lang java.base/java.lang.reflect"
                ) )
    }
}