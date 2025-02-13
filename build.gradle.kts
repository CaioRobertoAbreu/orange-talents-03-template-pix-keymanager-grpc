import com.google.protobuf.gradle.*
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    id("org.jetbrains.kotlin.kapt") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.micronaut.application") version "1.5.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.32"
    id("com.google.protobuf") version "0.8.15"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.4.32"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.5.0"
}

noArg {
    annotation("javax.persistence.Entity")
}

allOpen {
    annotation("io.micronaut.validation.Validated")
}

version = "0.1"
group = "br.com.zup.academy.caio"

val kotlinVersion=project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("br.com.zup.academy.caio.*")
    }
}

dependencies {
    kapt("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.grpc:micronaut-grpc-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.sql:micronaut-hibernate-jpa")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.sql:micronaut-vertx-mysql-client")
    implementation("io.micronaut.xml:micronaut-jackson-xml")
    implementation("io.micronaut:micronaut-http-client")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")


    implementation("io.micronaut:micronaut-validation")
    testImplementation("io.micronaut:micronaut-http-client")

    runtimeOnly("com.h2database:h2")
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.micronaut.test:micronaut-test-junit5:2.3.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation("org.mockito:mockito-core")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-params
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.2")

    //Jackson XML
    implementation("io.micronaut.xml:micronaut-jackson-xml")
    //Validator
    implementation("io.micronaut.beanvalidation:micronaut-hibernate-validator")

}


application {
    mainClass.set("br.com.zup.academy.caio.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }


}
sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.33.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
            }
        }
    }
}
