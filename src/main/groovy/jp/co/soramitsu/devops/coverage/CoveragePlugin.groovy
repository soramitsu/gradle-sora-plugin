package jp.co.soramitsu.devops.coverage

import jp.co.soramitsu.devops.SoraTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer

class CoveragePlugin implements Plugin<Project> {

    static final String JACOCO_PREFIX = "jacoco"
    static final String VERIFICATION_GROUP = "verification"

    @Override
    void apply(Project project) {
        project.pluginManager.apply(JacocoPlugin.class)

        // remove old jacoco tasks
        project.afterEvaluate { Project p ->
            p.tasks.removeIf({ Task t ->
                return t.name.startsWith(JACOCO_PREFIX)
            })
        }

        // create one 'coverage' task
        project.tasks.register(SoraTask.coverage, JacocoReport).configure { r ->
            r.group = VERIFICATION_GROUP
            r.description = "Collect code coverage and produce html/xml reports"

            r.reports({ JacocoReportsContainer c ->
                c.xml.enabled = true
                c.html.enabled = true
                c.csv.enabled = false
            })

            r.executionData(project
                    .fileTree(project.rootDir.absolutePath)
                    .include("**/build/jacoco/*.exec"))

            r.dependsOn([
                    SoraTask.build,
                    SoraTask.test
            ])
        }
    }
}
