import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    // Java support
    id("java")
    // Groovy support
    groovy
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.16.0"
    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "2.0.0"
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "0.1.13"
    // Gradle Kover Plugin
    id("org.jetbrains.kotlinx.kover") version "0.6.1"

}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Configure project's dependencies
repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public")
}

dependencies {
    implementation("com.google.inject:guice:7.0.0")
    implementation("org.apache.commons:commons-exec:1.3")

    implementation("com.google.guava:guava:32.1.3-jre")

    implementation("org.commonmark:commonmark:0.20.0")

    implementation("org.atmosphere:wasync:[3.0.2, 4.0.0)") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.atmosphere:atmosphere-runtime:+") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    compileOnly("com.intellij:annotations:+")

    testImplementation("org.slf4j:slf4j-simple:1.7.32")
    testImplementation("org.slf4j:slf4j-api:1.7.32")

    testImplementation("org.awaitility:awaitility:4.2.0")

    testImplementation(files("live-plugins/lib/LivePlugin.jar"))
    testImplementation("org.codehaus.groovy:groovy:3.0.13")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // Mockito for mocking in tests
    testImplementation("org.mockito:mockito-core:5.7.0")

    // If you want to use Mockito with JUnit Jupiter (JUnit 5), you can also add the Mockito JUnit Jupiter support
    testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")

}


// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin {
    jvmToolchain(17)
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) })
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl.set(properties("pluginRepositoryUrl"))
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
    cachePath.set(provider { file(".qodana").canonicalPath })
    reportPath.set(provider { file("build/reports/inspections").canonicalPath })
    saveReport.set(true)
    showReport.set(environment("QODANA_SHOW_REPORT").map { it.toBoolean() }.getOrElse(false))
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover.xmlReport {
    onCheck.set(true)
}


tasks {
    buildSearchableOptions {
        enabled = false
    }

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        })

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes.set(properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        })
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

    signPlugin {
        certificateChain.set(environment("CERTIFICATE_CHAIN"))
        privateKey.set(environment("PRIVATE_KEY"))
        password.set(environment("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(environment("PUBLISH_TOKEN"))
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(properties("pluginVersion").map {
            listOf(
                it.split('-').getOrElse(1) { "default" }.split('.').first()
            )
        })
    }

    sourceSets {
        getByName("main") {
            java {
                setSrcDirs(listOf("src/main/java"))
            }
        }

        getByName("test") {
            groovy {
                srcDirs(listOf("src/test/java", "live-plugins/groovy/.live-plugins"))
                exclude("**/classpath.index")
            }

            compileClasspath += sourceSets.main.get().output + sourceSets.main.get().compileClasspath
            runtimeClasspath += sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath
        }
    }

    named("koverXmlReport") {
        dependsOn("compileKotlin", "compileJava")
    }
}


gradle.taskGraph.whenReady {
    allTasks
        .filter { it.hasProperty("duplicatesStrategy") } // Because it's some weird decorated wrapper that I can't cast.
        .forEach {
            it.setProperty("duplicatesStrategy", "EXCLUDE")
        }
}
