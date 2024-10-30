import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  // Java support
  id("java")
  // Kotlin support
  id("org.jetbrains.kotlin.jvm") version "2.0.21"
  // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
  id("org.jetbrains.intellij.platform") version "2.1.0"
}

tasks.withType<KotlinCompile> {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_19)
  }
}

// Configure project's dependencies
repositories {
  mavenCentral()


  intellijPlatform {
    defaultRepositories()
    snapshots()
  }
}

dependencies {
  intellijPlatform {
    webstorm("2024.2.4", useInstaller = false)
    bundledPlugin("JavaScript")
    instrumentationTools()
    pluginVerifier()
    testFramework(TestFrameworkType.Platform)
  }
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.opentest4j:opentest4j:1.3.0")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellijPlatform {
  pluginConfiguration {
    group = "org.lso"
    name.set("LogIt")
    version.set("2024.31")
  }
  pluginVerification {
    failureLevel = VerifyPluginTask.FailureLevel.ALL
    verificationReportsDirectory = file("build/reports/pluginVerifier")
    verificationReportsFormats = VerifyPluginTask.VerificationReportsFormats.ALL
    teamCityOutputFormat = false
    subsystemsToCheck = VerifyPluginTask.Subsystems.ALL
    ides {
      ide(IntelliJPlatformType.WebStorm, "2024.2")
      recommended()
      select {
        types = listOf(IntelliJPlatformType.WebStorm)
        channels = listOf(ProductRelease.Channel.RELEASE)
        sinceBuild = "242"
        untilBuild = "243.*"
      }
    }
  }

  publishing {
    token.set(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
  }

  tasks {
    withType<JavaCompile> {
      sourceCompatibility = "19"
      targetCompatibility = "19"
    }

    patchPluginXml {
      changeNotes.set(
        """<br>
      v2024.31 - remove deprecated functions<br>
      v2024.3 - compatibility with 2024.3 version<br>
      v2024.21 - compatibility with 2024.202 version<br>
      v2024.2 - compatibility with 2024.2 version<br>
      v2024.1 - compatibility with 2024.1 version<br>
      v2023.3 - compatibility with 2023.3 version<br>
      v2023.21 - compatibility with 2023.2 version<br>
      v2023.1 - compatibility with 2023 version<br>
      v2022.2 - command to delete LogIt logs from file or project<br>
      v2022.1 - add patterns to add new info in the log line<br>
      v2021.1.2 - replace a deprecated api<br>
      v2021.1 - compatibility with 2021 version and the following ones<br>
      v2020.3.11 - new icon<br>
      v2020.3.1 - adding configuration settings<br>
      v2020.3 - to WebStorm 2020.3<br>
      v2020.2 - to WebStorm 2020.2<br>
      v201.1.1 - to WebStorm 2020.<br>
      v193.3.1 - some corrections when inserting a console.log from an empty line.<br>
      v1.0 - initial release.<br>
"""
      )
    }
  }
}


