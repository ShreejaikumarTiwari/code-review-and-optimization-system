plugins {
     id ("java")
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    

   
}

group = "com.codereviewer"
version = "0.0.1-SNAPSHOT"
description = "codereviewer and optimizer"
group = "com.converter"
version = "1.0.0"


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.github.javaparser:javaparser-core:3.25.8")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("org.apache.pdfbox:pdfbox:3.0.1")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation( "org.json:json:20240303")
    implementation("com.google.code.gson:gson:2.10.1")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}
tasks.withType<Test> {
    useJUnitPlatform()
}
