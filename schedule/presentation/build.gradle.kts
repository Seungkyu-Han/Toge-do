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
    implementation(project(":schedule:core"))

    //WEBFLUX
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //MONGODB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    //COROUTINE
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3")

    //REFLECT
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //SWAGGER
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}