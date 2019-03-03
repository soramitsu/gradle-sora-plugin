package jp.co.soramitsu.devops

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLoggingContainer
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer

class SoraPlugin implements Plugin<Project> {

    void apply(Project project) {
        setupJacocoPlugin(project)
        setupJavaPlugin(project)

        SoramitsuConfig c = project.extensions.create("soramitsu", SoramitsuConfig)
    }

    static void setupJacocoPlugin(Project project) {
        project.pluginManager.apply(JacocoPlugin.class)
        project.tasks.withType(JacocoReport.class).configureEach({ JacocoReport r ->
            r.reports({ JacocoReportsContainer c ->
                c.xml.enabled = true
                c.html.enabled = true
                c.csv.enabled = false
            })

            r.executionData(project
                    .fileTree(project.rootDir.absolutePath)
                    .include("**/build/jacoco/*.exec"))

            r.dependsOn(["build", "test"])
        })
    }

    static void setupJavaPlugin(Project project) {
        project.pluginManager.apply(JavaPlugin.class)
        project.plugins.withType(JavaPlugin.class, { JavaPlugin p ->
            project.tasks.named("build").configure { t ->
                t.dependsOn.remove("check")
                t.dependsOn.remove("test")
            }

            project.tasks.named("check").configure { t ->
                t.dependsOn("build")
            }

            project.tasks.withType(Test.class).configureEach { t ->
                t.testLogging({ TestLoggingContainer r ->
                    r.exceptionFormat = "full"
                })

                t.dependsOn("build")
            }
        })
    }
}
