/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.1.1/userguide/building_java_projects.html
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm")

    id("io.spring.dependency-management")
    id("org.springframework.boot")

    // spring-kotlin
    // kotlin: spring (proxy) related plugins see: https://kotlinlang.org/docs/reference/compiler-plugins.html
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.allopen")

  id("com.netflix.dgs.codegen")

}

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    //implementation("com.github.javafaker:javafaker:1.+")

    // dgs
    implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release"))
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}




tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    // see: https://netflix.github.io/dgs/generating-code-from-schema/

    language = "kotlin"
    generateClient = false
    packageName = "com.example.demo.generated"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
