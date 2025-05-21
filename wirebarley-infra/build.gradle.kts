description = "wirebarley-infra module"

tasks {
    bootJar { enabled = false }
    jar { enabled = true }
}

dependencies {
    val mysqlVersion: String by project
    val querydslVersion: String by project

    implementation(project(":wirebarley-core"))

    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("com.querydsl:querydsl-jpa")
    implementation("com.querydsl:querydsl-apt:$querydslVersion:jpa")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}