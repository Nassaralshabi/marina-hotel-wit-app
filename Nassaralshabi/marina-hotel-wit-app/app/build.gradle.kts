import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.marinahotel.kotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.marinahotel.kotlin"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1.0"
        
        // Enable vector drawables support for older devices
        vectorDrawables.useSupportLibrary = true
        
        // Multiplatform support
        multiDexEnabled = true
        
        // Test instrumentation runner
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Build config fields
        buildConfigField("String", "API_BASE_URL", "\"https://api.marina-hotel.com/\"") 
        buildConfigField("boolean", "DEBUG_MODE", "false")
    }

    // Signing configurations
    signingConfigs {
        getByName("debug") {
            keyAlias = "marina-debug"
            keyPassword = "marinahotel123"
            storeFile = file("debug.keystore")
            storePassword = "marinahotel123"
        }
        create("release") {
            // Load keystore properties
            val keystorePropsFile = rootProject.file("keystore.properties")
            if (keystorePropsFile.exists()) {
                val keystoreProps = Properties()
                keystoreProps.load(keystorePropsFile.inputStream())
                
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
                storeFile = file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
            } else {
                // Fallback to environment variables for CI/CD
                keyAlias = System.getenv("MARINA_KEY_ALIAS") ?: "marina-hotel-key"
                keyPassword = System.getenv("MARINA_KEY_PASSWORD") ?: "HotelApp@2024#Strong456"
                storeFile = file(System.getenv("MARINA_KEYSTORE_FILE") ?: "release.keystore")
                storePassword = System.getenv("MARINA_KEYSTORE_PASSWORD") ?: "Marina2024!SecureKey789"
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("boolean", "DEBUG_MODE", "true")
            buildConfigField("String", "API_BASE_URL", "\"https://dev-api.marina-hotel.com/\"") 
        }
        
        create("staging") {
            initWith(buildTypes.getByName("debug"))
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-STAGING"
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_BASE_URL", "\"https://staging-api.marina-hotel.com/\"") 
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Additional optimizations for release
            isZipAlignEnabled = true
            isCrunchPngs = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        
        // Kotlin compiler optimizations
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }

    // Build features
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    
    // Packaging options
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
        }
    }
    
    // Lint options
    lint {
        abortOnError = false
        checkReleaseBuilds = true
        ignoreWarnings = false
        warningsAsErrors = false
    }
    
    // Test options
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    kapt("androidx.room:room-compiler:2.6.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")
}
