buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        // TODO: Needed for Xposed API libs
        maven(url = "https://api.xposed.info")
    }
}
