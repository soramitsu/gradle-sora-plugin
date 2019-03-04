package jp.co.soramitsu.devops.coverage

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer

class CoveragePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply(JacocoPlugin.class)

        // remove old jacoco tasks
        project.afterEvaluate { Project p ->
            p.tasks.removeIf({ t ->
                return t.name.startsWith("jacoco")
            })
        }

        // create one 'coverage' task
        project.tasks.register("coverage", JacocoReport).configure { r ->
            r.group = "verification"
            r.description = "Collect code coverage and produce html/xml reports"

            r.reports({ JacocoReportsContainer c ->
                c.xml.enabled = true
                c.html.enabled = true
                c.csv.enabled = false
            })

            /// TODO: try uncomment for multi-project builds
//            project.subprojects.each {
//                r.sourceSets(it.sourceSets.main)
//            }

            r.executionData(project
                    .fileTree(project.rootDir.absolutePath)
                    .include("**/build/jacoco/*.exec"))

            r.dependsOn(["build", "test"])
        }
    }
}
