
pluginManagement {
    repositories {
        gradlePluginPortal()
    }


    val kotlinVersion = "1.5.21"
    val springBootVersion = "2.4.8"

    plugins {

        kotlin("jvm") version kotlinVersion

        // spring
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
        id("org.springframework.boot") version springBootVersion

        // spring-kotlin
        // kotlin: spring (proxy) related plugins see: https://kotlinlang.org/docs/reference/compiler-plugins.html
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.noarg") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion

        // dgs
        id("com.netflix.dgs.codegen") version "5.0.2"

        // misc
        id("com.github.ben-manes.versions") version "0.39.0"

    }



}

rootProject.name = "dgs-playground"
include("app")
