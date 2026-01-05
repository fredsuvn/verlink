plugins {
  id("java")
}

group = "space.sunqian.verlink"
version = "0.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.0")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.14.0")
  testImplementation("org.junit.platform:junit-platform-launcher:1.14.0")
}

tasks.test {
  useJUnitPlatform()
}