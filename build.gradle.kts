plugins {
    id("java")
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("jacoco")
}

group = "br.com.delivery"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("com.h2database:h2")
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}


// Task para gerar openapi.json
tasks.register("generateOpenApi") {
    group = "documentation"
    description = "Gera o arquivo openapi.json"
    
    doLast {
        val outputDir = file("${layout.buildDirectory.get()}/generated/openapi")
        outputDir.mkdirs()
        
        val openApiFile = file("$outputDir/openapi.json")
        
        // Executa a aplicação temporariamente para gerar o OpenAPI
        exec {
            commandLine("java", "-jar", "${layout.buildDirectory.get()}/libs/delivery-api-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=openapi")
        }
        
        // Baixa o arquivo OpenAPI do endpoint
        exec {
            commandLine("curl", "-o", openApiFile.absolutePath, "http://localhost:8080/v3/api-docs")
        }
        
        println("OpenAPI JSON gerado em: ${openApiFile.absolutePath}")
    }
    
    dependsOn("bootJar")
}

// Configuração do JaCoCo
jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.50".toBigDecimal()
            }
        }
    }
}

// Task para parar a aplicação após gerar o OpenAPI
tasks.register("stopApp") {
    group = "documentation"
    description = "Para a aplicação"
    
    doLast {
        exec {
            commandLine("pkill", "-f", "delivery-api")
        }
    }
}