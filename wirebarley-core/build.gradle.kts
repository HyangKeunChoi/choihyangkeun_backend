description = "wirebarley-core module"

tasks {
    bootJar { enabled = false }
    jar { enabled = true }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

// (Querydsl 설정부 추가 - start)
val generated = file("src/main/generated")
// querydsl QClass 파일 생성 위치를 지정
tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(generated)
}
// kotlin source set 에 querydsl QClass 위치 추가
sourceSets {
    main {
        java.srcDirs += generated
    }
}