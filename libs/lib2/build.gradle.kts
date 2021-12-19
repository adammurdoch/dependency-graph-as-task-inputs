plugins {
    id("java-library")
}

group = "test.libs"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-exec:1.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
}