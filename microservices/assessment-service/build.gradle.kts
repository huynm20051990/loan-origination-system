plugins {
    java
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.loan.origination.system.microservices"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

val mapstructVersion = "1.6.3"
var springCloudVersion = "2025.0.0"
val springAiVersion = "1.1.2"

repositories {
    mavenCentral()
}

tasks.named<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation(project(":api"))
    implementation(project(":util"))
    implementation(project(":contracts"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    // --- Spring AI ---
    implementation("org.springframework.ai:spring-ai-starter-model-google-genai")
    implementation("org.springframework.ai:spring-ai-starter-mcp-client-webflux")
    implementation("org.springframework.ai:spring-ai-starter-model-google-genai-embedding")
    implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")
    implementation("org.springframework.ai:spring-ai-advisors-vector-store")
    implementation("org.springframework.ai:spring-ai-starter-model-chat-memory-repository-cassandra")
    implementation("org.springaicommunity:tool-search-tool:1.0.1")
    implementation("org.springaicommunity:tool-searcher-lucene:1.0.1")
    // -----------------
    implementation("io.micrometer:micrometer-registry-prometheus")
    // --- KAFKA & CLOUD STREAM ---
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    // ------------------------------------------
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("org.postgresql:postgresql")
    implementation("com.pgvector:pgvector:0.1.6")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    // --- FOR TESTING WITH KAFKA/DB ---
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:junit-jupiter")
    // -------------------------------------------
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
