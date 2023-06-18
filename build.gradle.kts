plugins {
    id("java")
}

group = "oolloo"
version = "1.3.3"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}

tasks.jar {
    manifest {
        attributes ( mapOf (
            "Main-Class" to "oolloo.jlw.Wrapper",
            "Add-Opens" to "java.base/jdk.internal.loader"
                ) )
    }
}