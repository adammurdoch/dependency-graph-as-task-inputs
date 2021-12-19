plugins {
    id("java-gradle-plugin")
}

gradlePlugin {
    plugins {
        create("test") {
            id = "test.plugin"
            implementationClass = "TestPlugin"
        }
    }
}