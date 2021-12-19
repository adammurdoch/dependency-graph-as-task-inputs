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
    implementation("org.apache.commons:commons-collections4:4.4")
}