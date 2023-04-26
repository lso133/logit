import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  // Java support
  id("java")
  // Kotlin support
  id("org.jetbrains.kotlin.jvm") version "1.8.20"
  // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
  id("org.jetbrains.intellij") version "1.13.3"
}

group = "org.lso"
version = "v2023.1"

// Configure project's dependencies
repositories {
  mavenCentral()
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
  pluginName.set("LogIt")

  // see https://www.jetbrains.com/intellij-repository/releases/
  // and https://www.jetbrains.com/intellij-repository/snapshots/
  version.set("2023.2")
  type.set("IU")

  downloadSources.set(!System.getenv().containsKey("CI"))
  updateSinceUntilBuild.set(true)

  // Plugin Dependencies -> https://plugins.jetbrains.com/docs/intellij/plugin-dependencies.html
  // Example: platformPlugins = com.intellij.java, com.jetbrains.php:203.4449.22
  //
  plugins.set(listOf("JavaScript"))

  sandboxDir.set(project.rootDir.canonicalPath + "/.sandbox")

}

tasks {
  // Set the compatibility versions to 1.8
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    sinceBuild.set("223")
    untilBuild.set("231.*")
  }

  publishPlugin {
    token.set(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
  }

  runIde {
    ideDir.set(file("/Users/laurent/Library/Application Support/JetBrains/Toolbox/apps/WebStorm/ch-0/231.8109.174/WebStorm.app/Contents"))
  }

  patchPluginXml {
    changeNotes.set(
      """<br>
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


