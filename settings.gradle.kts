import java.nio.file.Paths

rootProject.name = "taskmates"

include("main", "test", "dev")
//includeFlat("livePlugins")
//project(":livePlugins").projectDir = file(".live-plugins")

//val modulesDir = File(rootDir, ".live-plugins")
//
//logger.error("modulesDir" + modulesDir.toString())
//
//modulesDir.listFiles()?.filter { file ->
//    file.isDirectory && file.name != "build" && file.name != "out" && file.name != "lib"
//}?.forEach { dir ->
//
//    val defaultBuildGradleTemplatePath = Paths.get(rootDir.path, ".live-plugins/build.gradle.kts")
//
//    // Create a default build.gradle.kts file if it does not exist
//    val buildGradleFile = File(dir, "build.gradle.kts")
////    if (!buildGradleFile.exists()) {
//    java.nio.file.Files.copy(
//        defaultBuildGradleTemplatePath,
//        buildGradleFile.toPath(),
//        java.nio.file.StandardCopyOption.REPLACE_EXISTING
//    )
////    }
//
//
//    val moduleName = dir.name.removePrefix(".")
//    include("live-plugins:$moduleName")
//
//    // Set the project directory for the included module
//    findProject(":live-plugins:$moduleName")?.projectDir = dir
//}
