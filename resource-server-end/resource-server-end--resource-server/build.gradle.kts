plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springframework.boot.spring.boot.devtools)
    implementation(libs.org.springframework.boot.spring.boot.starter.oauth2.resource.server)
    implementation(libs.org.springframework.boot.spring.boot.starter.data.jpa)
    runtimeOnly(libs.com.h2database.h2)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.io.rest.assured.rest.assured)
    testImplementation(libs.org.springframework.security.spring.security.test)
    testRuntimeOnly(libs.org.junit.platform.junit.platform.launcher)
}

group = "com.baeldung"
version = "0.1.0-SNAPSHOT"
description = "resource-server-end--resource-server"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
    exclude("**/*LiveTest.class")
}
