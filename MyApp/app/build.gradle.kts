plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.myappli"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myappli"
        minSdk = 24
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    //dataBinding{
    //  enabled = true
    //}
    buildToolsVersion = "30.0.3"
}

dependencies {
    implementation ("com.squareup.okhttp3:okhttp:3.14.7")
    implementation ("com.squareup.okio:okio:1.17.5")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-ui:2.7.5")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    //webSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    //implementation ("com.jzxiang.pickerview:TimePickerDialog:1.0.1")
    //implementation ("com.contrarywind:Android-PickerView:3.2.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}