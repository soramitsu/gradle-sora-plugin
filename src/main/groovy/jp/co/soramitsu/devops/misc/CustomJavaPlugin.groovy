package jp.co.soramitsu.devops.misc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLoggingContainer

class CustomJavaPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
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
