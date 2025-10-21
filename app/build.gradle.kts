plugins {
    id("com.android.application")
}

dependencies {
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.annotation:annotation:1.9.0")
    implementation("androidx.loader:loader:1.1.0")
    implementation("androidx.fragment:fragment:1.7.1")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.crossbowffs.remotepreferences:remotepreferences:0.8")
    compileOnly("de.robv.android.xposed:api:82")
    compileOnly("de.robv.android.xposed:api:82:sources")
}

android {
    namespace = "com.crossbowffs.nekosms"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        versionCode = 43
        versionName = "0.24.1-dev"
        minSdk = 19
        targetSdk = 34
        buildConfigField("int", "MODULE_VERSION", "19")
        buildConfigField("int", "DATABASE_VERSION", "12")
        buildConfigField("int", "BACKUP_VERSION", "3")
        buildConfigField("String", "LOG_TAG", "\"NekoSMS\"")
    }

    androidResources {
        localeFilters.addAll(listOf("en", "zh-rCN", "ru"))
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("int", "LOG_LEVEL", "2")
            buildConfigField("boolean", "LOG_TO_XPOSED", "false")
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFile("proguard-rules.pro")
            buildConfigField("int", "LOG_LEVEL", "4")
            buildConfigField("boolean", "LOG_TO_XPOSED", "true")
        }
    }
}
