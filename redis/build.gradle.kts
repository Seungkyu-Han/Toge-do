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

    //REDIS
    implementation ("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.1.2")

    //MONGODB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    //COROUTINE
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3")

    //TEST
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation ("org.mockito:mockito-core")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}