plugins {
    java
    jacoco
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ai.llamacpprag"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

ext {
    set("springAiVersion", "2.0.0-M1")
    set("testcontainersVersion", "1.20.4")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")

    // Spring AI (OpenAI-compatible) — we point it to llama.cpp base-url
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.springframework.ai:spring-ai-rag")
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")

    // PDF Rag
    implementation("org.apache.pdfbox:pdfbox:3.0.3")

    // Vector store starter (pgvector)
    implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")

    // Vector store: PostgresSQL + pgvector
    implementation("org.postgresql:postgresql:42.7.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/LlamacppRagApplication.class")
            }
        })
    )
}
