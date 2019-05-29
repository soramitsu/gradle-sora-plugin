package jp.co.soramitsu.devops.misc

import groovy.transform.CompileStatic
import jp.co.soramitsu.devops.SoraTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLoggingContainer

@CompileStatic
class CustomJavaPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply(JavaPlugin.class)
        project.plugins.withType(JavaPlugin.class, { JavaPlugin p ->
            project.tasks.named(SoraTask.build).configure { t ->
                // build should not depend on check/test
                t.dependsOn.remove(SoraTask.check)
                t.dependsOn.remove(SoraTask.test)
            }

            project.tasks.named(SoraTask.coverage).configure { t ->
                t.dependsOn(SoraTask.build)
            }

            project.tasks.withType(Test.class).configureEach { t ->
                t.testLogging({ TestLoggingContainer r ->
                    r.exceptionFormat = "full"
                })

                t.dependsOn(SoraTask.build)
            }
        })
    }
}
