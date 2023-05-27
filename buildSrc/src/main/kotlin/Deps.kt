object Version {
    const val compileSdk = ""
    const val minSdk = ""
    const val targetSdk = ""

    const val coreKtx = "1.7.0"
    const val material = "1.4.0"
    const val camerax = "1.0.2"
    const val junit = "4.13"
    const val espresso = "3.4.0"
    const val junitExt = "1.1.3"
    const val appCompat = "1.3.1"
    const val constraintLayout = "2.1.1"
    const val mlKit = "16.0.0"
    const val exifInterface = "1.2.0"
    const val legacy = "1.0.0"
    const val fragmentKtx = "1.5.7"
    const val lifecyclelKtx = "2.6.1"
    const val hilt = "2.44"
}

object Libs {
    object Android {
        const val core = "androidx.core:core-ktx:${Version.coreKtx}"
        const val appCompat = "androidx.appcompat:appcompat:${Version.appCompat}"
        const val material = "com.google.android.material:material:${Version.material}"
        const val exifInterface =  "androidx.exifinterface:exifinterface:${Version.exifInterface}"
        const val legacy = "androidx.legacy:legacy-support-v4:${Version.legacy}"
        const val junit = "junit:junit:${Version.junit}"
        const val junitText = "androidx.test.ext:junit:${Version.junitExt}"
        const val espresso = "androidx.test.espresso:espresso-core:${Version.espresso}"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:${Version.fragmentKtx}"
        const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.lifecyclelKtx}"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.lifecyclelKtx}"
    }

    object MlKit {
        const val text = "com.google.android.gms:play-services-mlkit-text-recognition:${Version.mlKit}"
    }

    object Camerax {
        const val camera2 = "androidx.camera:camera-camera2:${Version.camerax}"
        const val lifecycle = "androidx.camera:camera-lifecycle:${Version.camerax}"
        const val view = "androidx.camera:camera-view:1.0.0-alpha27"
    }

    object Di {
        const val hilt = "com.google.dagger:hilt-android:${Version.hilt}"
        const val hiltKapt = "com.google.dagger:hilt-android-compiler:${Version.hilt}"
    }
    object Picker {
        const val image = "com.github.dhaval2404:imagepicker:2.1"
    }


}