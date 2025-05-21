description = "wirebarley-core module"

tasks {
    bootJar { enabled = false }
    jar { enabled = true }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}
