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
    //USER PERSISTENCE
    implementation(project(":user:persistence"))
    implementation(project(":security"))

    //WEBFLUX
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //MONGODB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    //AWS S3
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")


    //TEST
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation ("org.mockito:mockito-core")

    //COROUTINE
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3")

    //REFLECT
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //REDIS
    implementation ("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.1.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.bootJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
