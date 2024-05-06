plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.typ.muslim"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.typ.muslim"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.1.3"
        multiDexEnabled = true

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }

    kotlin {
        jvmToolchain(JavaVersion.VERSION_17.ordinal + 1)
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{*}"
        }
    }
}

dependencies {
    // Local libs
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Android CoreX libraries
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    implementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    // Google libraries
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:2.3.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.2")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.2")
    implementation("io.ktor:ktor-server-default-headers-jvm:2.3.2")
    // Jetpack Room db
    ksp("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.work:work-runtime:2.8.1")
    implementation("android.arch.persistence.room:runtime:1.1.1")
    annotationProcessor("android.arch.persistence.room:compiler:1.1.1")
    // Jetpack compose
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.7.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.01"))
    // Third party libraries
    implementation("com.factor:bouncy:1.8")
    implementation("com.milaptank:stv:1.0.0")
    implementation("com.jraska:falcon:2.2.0")
    implementation("com.bitvale:switcher:1.1.0")
    implementation("com.haibin:calendarview:3.7.1")
    implementation("com.evernote:android-job:1.4.3")
    implementation("com.jude:easyrecyclerview:4.4.2")
    implementation("com.github.smart-fun:Perm:1.2.0")
    implementation("com.airbnb.android:lottie:6.0.1")
    implementation("com.irozon.sneaker:sneaker:2.0.0")
    implementation("com.github.perfomer:blitz:1.0.10")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.iwgang:simplifyspan:2.2")
    implementation("com.emc.thye:VerticalCalendar:1.0.4")
    implementation("com.github.MehdiKh93:Shortcut:1.0.2")
    implementation("com.github.zyyoona7:wheelview:1.0.7")
    implementation("com.sdsmdg.harjot:rotatingtext:1.0.2")
    implementation("com.irozon.alertview:alertview:1.0.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.iwgang:countdownview:2.1.6")
    implementation("com.daimajia.slider:library:1.1.5@aar")
    implementation("com.github.markshawn:auto-switcher:1.2")
    implementation("com.diogobernardino:williamchart:3.11.0")
    implementation("com.liaoinstan.springview:library:1.7.0")
    implementation("com.github.sarnavakonar:TextWriter:v1.0")
    implementation("kr.co.prnd:stepprogressbar:1.0.1")
    implementation("com.furkanakdemir:surroundcardview:1.0.6")
    implementation("com.sothree.slidinguppanel:library:3.4.0")
    implementation("bg.devlabs.transitioner:transitioner:1.3")
    implementation("jp.wasabeef:recyclerview-animators:4.0.2")
    implementation("com.michalsvec:single-row-calednar:1.0.0")
    implementation("com.zekapp.library:progreswheelview:1.1.5")
    implementation("com.github.tushar09:Gradient-Textview:1.1")
    implementation("com.github.zagum:Android-ExpandIcon:1.3.0")
    implementation("com.github.florent37:expansionpanel:1.2.4")
    implementation("com.github.zhpanvip:BannerViewPager:3.5.0")
    implementation("com.jackandphantom.android:blurimage:1.2.0")
    implementation("cn.aigestudio.wheelpicker:WheelPicker:1.1.3")
    implementation("net.alhazmy13.hijridatepicker:library:3.0.0")
    implementation("com.github.AnthonyFermin:DropDownView:1.0.1")
    implementation("com.github.mjn1369:ScaleTouchListener:1.0.0")
    implementation("com.github.alxrm:audiowave-progressbar:0.9.2")
    implementation("com.liaoinstan.springview:WeixinHeader:1.7.0")
    implementation("com.github.BirjuVachhani:locus-android:3.0.1")
    implementation("com.github.SumiMakito:AdvancedTextSwitcher:0.3")
    implementation("com.paulrybitskyi.valuepicker:valuepicker:1.0.3")
    implementation("com.crowdfire.cfalertdialog:cfalertdialog:1.1.0")
    implementation("com.github.kapil93:circular-layout-manager:2.0.0")
    implementation("com.flyco.tablayout:FlycoTabLayout_Lib:2.1.2@aar")
    implementation ("com.amitshekhar.android:android-networking:1.0.2")
    implementation("com.amulyakhare:com.amulyakhare.textdrawable:1.0.1")
    implementation("com.diogobernardino.williamchart:tooltip-slider:3.11.0")
    implementation("com.diogobernardino.williamchart:tooltip-points:3.11.0")
    implementation("com.github.AdityaAnand1:Morphing-Material-Dialogs:0.0.3")
    implementation("com.readystatesoftware.sqliteasset:sqliteassethelper:2.0.1")
}