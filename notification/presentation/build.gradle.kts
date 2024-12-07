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
    //PROJECT
    implementation(project(":notification:core"))

    //WEBFLUX
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //SWAGGER
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}