plugins {
    kotlin("jvm")
}
repositories {
    mavenCentral()
}

dependencies {
    //JWT
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}