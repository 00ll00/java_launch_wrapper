plugins {
    id("java")
}

group = "oolloo"
version = "1.4.0"

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
            "Add-Opens" to "java.base/jdk.internal.loader"
                ) )
    }
}