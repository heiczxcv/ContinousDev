plugins {
	java
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.heic"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	
	// OpenCV for face detection
	implementation("org.openpnp:opencv:4.7.0-0")
	
	// Deep Java Library for face recognition (patched version)
	implementation("ai.djl:api:0.31.1")
	implementation("ai.djl.opencv:opencv:0.31.1")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
