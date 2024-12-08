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

    implementation(project(":security:core"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    if (System.getProperty("os.name") == "Mac OS X" && System.getProperty("os.arch") == "aarch64") {
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.94.Final:osx-aarch_64")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation ("org.mockito:mockito-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

dependencyManagement{
    imports{
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}