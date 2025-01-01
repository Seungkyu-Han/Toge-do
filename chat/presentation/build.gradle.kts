plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":chat:core"))

    //WEBFLUX
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //WEBSOCKET
    implementation ("org.springframework.boot:spring-boot-starter-websocket")

    //COROUTINE
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3")

    //REFLECT
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //SWAGGER
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")

    //MAC NETTY RESOLVER
    if(System.getProperty("os.name") == "Mac OS X" && System.getProperty("os.arch") == "aarch64"){
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.94.Final:osx-aarch_64")
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}