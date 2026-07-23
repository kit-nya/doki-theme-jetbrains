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
    url = uri("https://maven.pkg.github.com/kit-nya/doki-build-source-jvm")
    credentials {
      username = System.getenv("GITHUB_ACTOR") ?: ""
      password = System.getenv("GITHUB_TOKEN") ?: ""
    }
  }
}

dependencies {
  implementation("org.jsoup:jsoup:1.17.2")
  implementation("com.google.code.gson:gson:2.10.1")
  // No release tag exists on kit-nya/doki-build-source-jvm yet; pin to the commit that
  // corresponds to the original v88.0.6 tag so JitPack resolves the exact same source.
  implementation("com.github.kit-nya:doki-build-source-jvm:94f85b6d345e1ea968b3aa2255ccf1f8d740b97c")
}
