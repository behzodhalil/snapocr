plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace  = "uz.behzoddev.snapocr"
    compileSdk = 33

    defaultConfig {
        applicationId  ="uz.behzoddev.snapocr"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName  = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = ("1.8")
    }
    packagingOptions {
        exclude ("META-INF/androidx.exifinterface_exifinterface.version")
    }

    viewBinding.isEnabled = true
}

dependencies {
    with(Libs.Android) {
        implementation(core)
        implementation(appCompat)
        implementation(material)
        implementation(exifInterface)
        implementation(legacy)
        implementation(viewModelKtx)
        implementation(fragmentKtx)
        implementation(lifecycleKtx)

        testImplementation(junit)

        androidTestImplementation(junitText)
        androidTestImplementation(espresso)
    }

    with(Libs.Camerax) {
        implementation(camera2)
        implementation(Libs.Camerax.lifecycle)
        implementation(view)
    }

    with(Libs.MlKit) {
        implementation(text)
    }

    with(Libs.Di) {
        implementation(hilt)
        kapt(hiltKapt)
    }
    
    with(Libs.Picker) {
        implementation(image)
    }
}
kapt {
    correctErrorTypes = true
}