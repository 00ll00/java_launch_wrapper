plugins {
    id("java")
}

group = "oolloo"
version = "1.3.4"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}

tasks.register<Exec>("zigBuild") {
    mustRunAfter("clearLibs")
    commandLine("zig", "build", "-Doptimize=ReleaseFast")
}

tasks.register<Copy>("copyLibs") {
    mustRunAfter("zigBuild")
    from("zig-out/lib")
    include("**/*.dll")
    into("src/main/resources")
}

tasks.register<Delete>("clearLibs") {
    delete("zig-out/lib", "src/main/resources")
}

tasks.jar {

    dependsOn("clearLibs", "zigBuild", "copyLibs")

    manifest {
        attributes ( mapOf (
            "Main-Class" to "oolloo.jlw.Wrapper",
            "Add-Opens" to "java.base/jdk.internal.loader"
                ) )
    }
}