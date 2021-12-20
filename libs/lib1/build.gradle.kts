plugins {
    id("java-library")
}

group = "test.libs"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:31.0.1-jre")
}
