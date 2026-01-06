plugins {
  id("java")
  id("application")
}

group = "space.sunqian.verlink"
version = "0.0.0"

repositories {
  mavenCentral()
}

application {
  mainClass.set("space.sunqian.verlink.VerLinkFrame")
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.0")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.14.0")
  testImplementation("org.junit.platform:junit-platform-launcher:1.14.0")
}

tasks.test {
  useJUnitPlatform()
}

tasks.register("createExe", Exec::class) {
  group = "distribution"
  description = "Creates a Windows executable using jpackage."
  dependsOn("jar")

  val jarFile = tasks.jar.get().archiveFile.get().asFile
  val mainClass = application.mainClass.get()
  val version = project.version.toString()
  val outputDir = file("build/dist")
  val appDir = file("$outputDir/VerLink")
  val iconFile = file("src/main/resources/logo.ico")

  doFirst {
    if (appDir.exists()) {
      delete(appDir)
    }
  }

  outputs.dir(outputDir)

  // build by jpackage command
  commandLine = listOf(
    "jpackage",
    "--type", "app-image",
    "--app-version", version,
    "--input", jarFile.parent.toString(),
    "--dest", outputDir.toString(),
    "--name", "VerLink",
    "--main-jar", jarFile.name,
    "--main-class", mainClass,
    "--icon", iconFile.absolutePath,
    "--add-modules", "java.base,java.desktop,java.xml",
    "--jlink-options", "--strip-debug",
    "--verbose"
  )
}

tasks.register("createZip", Exec::class) {
  group = "distribution"
  description = "Creates a ZIP archive of the application"
  dependsOn("createExe")

  val version = project.version.toString()
  val outputDir = file("build/dist")
  val appImageDir = file("$outputDir/VerLink")
  val zipOutput = file("$outputDir/verlink-${version}-win.zip")
  val zipScript = file("buildx/zip.bat")

  outputs.file(zipOutput)

  commandLine = mutableListOf(
    "cmd.exe",
    "/c",
    zipScript.absolutePath,
    appImageDir.absolutePath,
    zipOutput.absolutePath
  )
}