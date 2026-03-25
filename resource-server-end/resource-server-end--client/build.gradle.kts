plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springframework.boot.spring.boot.starter.thymeleaf)
    implementation(libs.org.springframework.boot.spring.boot.devtools)
    implementation(libs.org.springframework.spring.webflux)
    implementation(libs.io.projectreactor.netty.reactor.netty)
    implementation(libs.org.springframework.boot.spring.boot.starter.oauth2.client)
    implementation(libs.org.thymeleaf.extras.thymeleaf.extras.springsecurity6)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.springframework.security.spring.security.test)
    testImplementation(libs.io.rest.assured.rest.assured)
    testImplementation(libs.com.squareup.okhttp3.mockwebserver)
    testRuntimeOnly(libs.org.junit.platform.junit.platform.launcher)
}

group = "com.baeldung"
version = "0.1.0-SNAPSHOT"
description = "resource-server-end--client"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
    exclude("**/*LiveTest.class")
}
