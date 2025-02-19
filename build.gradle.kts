plugins {
    kotlin("jvm") version "2.0.21"
}

group = "ru.enzhine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.hadoop:hadoop-common:2.7.7")
    implementation("org.apache.hadoop:hadoop-mapreduce-client-core:2.7.7")
}

kotlin {
    jvmToolchain(17)
}