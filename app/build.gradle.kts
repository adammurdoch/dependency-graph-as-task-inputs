plugins {
    id("application")
    id("test.plugin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":util"))
    implementation("test.libs:lib1:1.0")
}