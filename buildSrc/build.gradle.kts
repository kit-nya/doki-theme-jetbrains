plugins {
  kotlin("jvm") version "2.1.20"
  `kotlin-dsl`
}

repositories {
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
  maven {
    url = uri("https://jitpack.io")
  }
  maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/doki-theme/doki-build-source-jvm")
    credentials {
      username = System.getenv("GITHUB_ACTOR") ?: ""
      password = System.getenv("GITHUB_TOKEN") ?: ""
    }
  }
}

dependencies {
  implementation("org.jsoup:jsoup:1.17.2")
  implementation("com.google.code.gson:gson:2.10.1")
  implementation("com.github.doki-theme:doki-build-source-jvm:v88.0.6")
}
