plugins {
    java
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
}


allprojects {
    apply(plugin = "java")

    group = "choihyangkeun_backend"
    version = "0.0.1-SNAPSHOT"

    configurations {
        all {
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
            exclude(group = "io.undertow", module = "undertow-websockets-jsr")
        }
    }

    repositories {
        mavenCentral()
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

val projects = listOf(
    project(":wirebarley-core"),
    project(":wirebarley-infra")
)

configure(projects) {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }
}

val applicationProjects = listOf(
    project(":wirebarley-api"),
)

configure(applicationProjects) {

    dependencies {
        implementation(project(":wirebarley-core"))
        implementation(project(":wirebarley-infra"))

        val springCloudDependenciesVersion: String by project
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
        implementation("org.springframework:spring-tx")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("org.springframework.boot:spring-boot-starter-undertow")

        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation(enforcedPlatform("org.springframework.cloud:spring-cloud-dependencies:$springCloudDependenciesVersion"))
        compileOnly("org.projectlombok:lombok:1.18.24")
        annotationProcessor("org.projectlombok:lombok:1.18.24")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }
}
