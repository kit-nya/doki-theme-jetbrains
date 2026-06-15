import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import java.util.Base64

fun decodeBase64(value: String?): String? {
  return value?.let {
    try {
      String(Base64.getDecoder().decode(it.trim()))
    } catch (e: Exception) {
      it // If it's not base64, return it as-is
    }
  }
}

plugins {
  // Custom plugin for building all the themes
  id("doki-theme-plugin")
  id("java") // Java support
  alias(libs.plugins.kotlin) // Kotlin support
  alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
  alias(libs.plugins.changelog) // Gradle Changelog Plugin
  alias(libs.plugins.kover) // Gradle Kover Plugin
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// Set the JVM language level used to build the project.
kotlin {
  jvmToolchain(21)
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

// Configure project's dependencies
repositories {
  mavenCentral()

  // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
  intellijPlatform {
    defaultRepositories()
  }
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
  implementation("commons-io:commons-io:2.17.0")
  implementation("org.javassist:javassist:3.29.2-GA")
  implementation("io.sentry:sentry:7.17.0")
  testImplementation("org.assertj:assertj-core:3.25.3")
  testImplementation("io.mockk:mockk:1.13.13")
  testImplementation(libs.junit)
  testImplementation(libs.opentest4j)

  // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
  intellijPlatform {
    intellijIdea(providers.gradleProperty("platformVersion"))

    // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
    bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
    plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

    // Module Dependencies. Uses `platformBundledModules` property from the gradle.properties file for bundled IntelliJ Platform modules.
    bundledModules(providers.gradleProperty("platformBundledModules").map { it.split(',') })

    testFramework(TestFrameworkType.Platform)
  }
}

configurations {
  implementation.configure {
    // sentry brings in a slf4j that breaks when
    // with the platform slf4j
    exclude("org.slf4j")
  }
}

// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
  instrumentCode = true
  buildSearchableOptions = false
  pluginConfiguration {
    name = providers.gradleProperty("pluginName")
    version = providers.gradleProperty("pluginVersion")

    // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
    description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
      val start = "<!-- Plugin description -->"
      val end = "<!-- Plugin description end -->"

      with(it.lines()) {
        if (!containsAll(listOf(start, end))) {
          throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
        }
        subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
      }
    }

    val changelog = project.changelog
    // Get the latest available change notes from the changelog file
    changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
      with(changelog) {
        val item = getOrNull(pluginVersion)
        if (item != null) {
          renderItem(
            item.withHeader(false).withEmptySections(false),
            Changelog.OutputType.HTML,
          )
        } else {
          "No change notes for version $pluginVersion"
        }
      }
    }

    ideaVersion {
      sinceBuild = providers.gradleProperty("pluginSinceBuild")
    }
  }

  signing {
    certificateChain.set(
      providers.environmentVariable("CERTIFICATE_CHAIN")
        .filter { it.isNotBlank() }
        .map { decodeBase64(it) }
    )
    privateKey.set(
      providers.environmentVariable("PRIVATE_KEY")
        .filter { it.isNotBlank() }
        .map { decodeBase64(it) }
    )
    password.set(
      providers.environmentVariable("PRIVATE_KEY_PASSWORD")
        .filter { it.isNotBlank() }
    )
  }

  publishing {
    token.set(
      providers.environmentVariable("PUBLISH_TOKEN")
        .filter { it.isNotBlank() }
    )
    channels.set(providers.gradleProperty("pluginVersion").map { 
      val channel = it.substringAfter('-', "").substringBefore('.')
      if (channel.isEmpty() || channel.all { c -> c.isDigit() }) {
        listOf("stable")
      } else {
        listOf(channel)
      }
    })
  }

  pluginVerification {
    ides {
      create(IntelliJPlatformType.IntellijIdeaUltimate, providers.gradleProperty("platformVersion"))
    }
  }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
  path.set(file("CHANGELOG.md").path)
  headerParserRegex.set("""^\[?([^\]\s]+)\]?.*""".toRegex())
  groups.empty()
  repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
  versionPrefix = ""
}

// Configure Gradle Kover Plugin - read more: https://kotlin.github.io/kotlinx-kover/gradle-plugin/#configuration-details
kover {
  reports {
    total {
      xml {
        onCheck = true
      }
    }
  }
}

configurations.all {
  resolutionStrategy {
    force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    force("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.1")
    force("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.8.1")
  }
}

tasks {
  test {
    jvmArgs("-Dkotlinx.coroutines.debug=off")
  }

  wrapper {
    gradleVersion = providers.gradleProperty("gradleVersion").get()
  }

  patchPluginXml {
    dependsOn("buildThemes")
  }

  publishPlugin {
    dependsOn(patchChangelog)
  }
}

intellijPlatformTesting {
  runIde {
    register("runIdeForUiTests") {
      task {
        jvmArgumentProviders += CommandLineArgumentProvider {
          listOf(
            "-Drobot-server.port=8082",
            "-Dide.mac.message.dialogs.as.sheets=false",
            "-Djb.privacy.policy.text=<!--999.999-->",
            "-Djb.consents.confirmation.enabled=false",
          )
        }
      }

      plugins {
        robotServerPlugin()
      }
    }
  }
}
