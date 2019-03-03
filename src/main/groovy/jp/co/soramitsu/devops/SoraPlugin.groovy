package jp.co.soramitsu.devops

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer

class SoraPlugin implements Plugin<Project> {

    void apply(Project project) {
        setupJacoco(project)

        SoramitsuConfig c = project.extensions.create("soramitsu", SoramitsuConfig)
    }

    static void setupJacoco(Project project) {
        project.getPluginManager().apply(JacocoPlugin.class)
        project.getTasks().withType(JacocoReport.class).configureEach({ JacocoReport r ->
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



}
