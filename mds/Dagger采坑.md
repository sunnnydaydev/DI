# app/build.gradle文件

```kotlin
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
}

android {
    namespace 'com.carry.direview'
    compileSdk 34

    defaultConfig {
        applicationId "com.carry.direview"
        minSdk 29
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {

    implementation 'androidx.core:core-ktx:1.10.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.activity:activity:1.8.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'

    // dagger
    implementation 'com.google.dagger:dagger:2.38.1'
    kapt 'com.google.dagger:dagger-compiler:2.38.1'
}
```

# 报错

build通过，run不起来，log如下

```kotlin

Execution failed for task ':direview:kaptDebugKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask$KaptExecutionWorkAction
> java.lang.reflect.InvocationTargetException (no error message)

Caused by: org.gradle.workers.internal.DefaultWorkerExecutor$WorkExecutionException: A failure occurred while executing org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask$KaptExecutionWorkAction

Caused by: java.lang.reflect.InvocationTargetException

Caused by: com.sun.tools.javac.processing.AnnotationProcessingError

Caused by: java.lang.IllegalStateException: Unsupported metadata version. Check that your Kotlin version is >= 1.0        

```

# 解决

看最后一个Caused by 获取到点信息。这里没按照提示去改，更新到如下版本顺利run起来了

```kotlin
    implementation 'com.google.dagger:dagger:2.45'
    kapt 'com.google.dagger:dagger-compiler:2.44'
```