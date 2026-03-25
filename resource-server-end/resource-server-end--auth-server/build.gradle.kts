plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springframework.boot.spring.boot.starter.actuator)
    implementation(libs.org.springframework.boot.spring.boot.starter.data.jpa)
    implementation(libs.org.jboss.resteasy.resteasy.jackson2.provider)
    implementation(libs.org.keycloak.keycloak.dependencies.server.all) {
        // TODO: This exclude was sourced from a POM exclusion and is NOT exactly equivalent, see: https://docs.gradle.org/9.4.1/userguide/build_init_plugin.html#sec:pom_maven_conversion
        exclude(mapOf("group" to "org.slf4j", "module" to "slf4j-log4j12"))

        // TODO: This exclude was sourced from a POM exclusion and is NOT exactly equivalent, see: https://docs.gradle.org/9.4.1/userguide/build_init_plugin.html#sec:pom_maven_conversion
        exclude(mapOf("group" to "log4j", "module" to "log4j"))
    }
    implementation(libs.org.keycloak.keycloak.crypto.default)
    implementation(libs.org.keycloak.keycloak.admin.ui)
    implementation(libs.org.keycloak.keycloak.services)
    compileOnly(libs.org.springframework.boot.spring.boot.configuration.processor)
    runtimeOnly(libs.com.h2database.h2)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.io.rest.assured.rest.assured)
    compileOnly(libs.org.keycloak.keycloak.servlet.filter.adapter)
    testRuntimeOnly(libs.org.junit.platform.junit.platform.launcher)
}

group = "com.baeldung"
version = "0.1.0-SNAPSHOT"
description = "resource-server-end--auth-server"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
    exclude("**/*LiveTest.class")
}
