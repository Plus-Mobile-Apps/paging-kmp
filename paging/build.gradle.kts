import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.maven.publish)
}

kotlin {
    android {
        namespace = "com.plusmobileapps.paging"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
        withHostTest {}
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.konnectivity)
            implementation(libs.multiplatform.settings.noArg)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.multiplatform.settings.test)
            implementation(libs.kotest.assertions.core)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(artifactId = "paging")

    pom {
        name.set("Paging KMP")
        description.set("A Kotlin Multiplatform paging helper for in-memory or cached data sources.")
        url.set("https://github.com/plusmobileapps/paging-kmp")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("plusmobileapps")
                name.set("Andrew Steinmetz")
                email.set("andrew@plusmobileapps.com")
            }
        }
        scm {
            url.set("https://github.com/plusmobileapps/paging-kmp")
            connection.set("scm:git:git://github.com/plusmobileapps/paging-kmp.git")
            developerConnection.set("scm:git:ssh://git@github.com/plusmobileapps/paging-kmp.git")
        }
    }
}
