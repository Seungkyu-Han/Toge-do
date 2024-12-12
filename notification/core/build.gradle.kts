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
    //WEBFLUX
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //REFLECT
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //MAIL
    implementation("org.springframework.boot:spring-boot-starter-mail:3.4.0")

    //KAFKA
    implementation("io.projectreactor.kafka:reactor-kafka")
    implementation("org.springframework.kafka:spring-kafka")

    //FCM
    implementation("com.google.firebase:firebase-admin:9.4.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}