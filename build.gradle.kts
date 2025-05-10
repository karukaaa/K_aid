// build.gradle.kts (project level)
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        classpath ("com.google.firebase:perf-plugin:1.4.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
   // id("com.google.firebase.firebase-perf") version "1.4.2" apply false
}