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
    //JWT
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}