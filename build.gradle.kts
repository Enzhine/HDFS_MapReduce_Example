plugins {
    kotlin("jvm") version "2.0.21"
}

group = "ru.enzhine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.github.rholder:snowball-stemmer:1.3.0.581.1")
    implementation("org.apache.hadoop:hadoop-common:2.7.7")
    implementation("org.apache.hadoop:hadoop-mapreduce-client-core:2.7.7")
}

kotlin {
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType(Jar::class.java) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes("Main-Class" to "ru.enzhine.WordsDriver")
    }

    from(configurations.compileClasspath.map { cfg -> cfg.map { if (it.isDirectory) it else zipTree(it) } })
}