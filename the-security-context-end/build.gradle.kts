plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springframework.boot.spring.boot.starter.tomcat)
    implementation(libs.org.springframework.boot.spring.boot.starter.thymeleaf)
    implementation(libs.nz.net.ultraq.thymeleaf.thymeleaf.layout.dialect)
    implementation(libs.org.springframework.boot.spring.boot.devtools)
    implementation(libs.org.springframework.spring.context.support)
    implementation(libs.org.springframework.boot.spring.boot.starter.security)
    implementation(libs.org.springframework.boot.spring.boot.starter.validation)
    implementation(libs.org.passay.passay)
    implementation(libs.org.springframework.boot.spring.boot.starter.data.jpa)
    implementation(libs.org.springframework.boot.spring.boot.starter.mail)
    implementation(libs.com.google.guava.guava)
    runtimeOnly(libs.org.hsqldb.hsqldb)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.junit.vintage.junit.vintage.engine) {
        exclude(mapOf("group" to "org.hamcrest", "module" to "hamcrest-core"))
    }
    testImplementation(libs.org.springframework.security.spring.security.test)
    testRuntimeOnly(libs.org.junit.platform.junit.platform.launcher)
    compileOnly(libs.org.apache.tomcat.embed.tomcat.embed.jasper)
}

group = "com.baeldung"
version = "0.1.0-SNAPSHOT"
description = "the-security-context-end"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}
