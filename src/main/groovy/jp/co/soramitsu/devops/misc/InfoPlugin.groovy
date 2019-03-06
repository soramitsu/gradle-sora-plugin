package jp.co.soramitsu.devops.misc

import com.palantir.gradle.gitversion.GitVersionPlugin
import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.SoramitsuExtension
import jp.co.soramitsu.devops.utils.PrintUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class InfoPlugin implements Plugin<Project> {

    static final String INFO_GROUP_NAME = "info"

    @Override
    void apply(Project project) {
        project.pluginManager.apply(GitVersionPlugin.class)

        // set project version based on git
        project.version = project.gitVersion()

        project.tasks.named(SoraTask.printVersion).configure { Task t ->
            t.group = INFO_GROUP_NAME
            t.description = "Print git version information"
        }

        project.tasks.register(SoraTask.printOsInfo).configure { Task t ->
            t.group = INFO_GROUP_NAME
            t.description = "Print OS and version information"
            t.doLast {
                PrintUtils.printBanner(project)
            }
        }

        project.tasks.register(SoraTask.printConfig).configure { Task t ->
            t.group = INFO_GROUP_NAME
            t.description = "Print plugin configuration"

            def ext = project.extensions.getByName("soramitsu")
            t.doLast {
                println("""
    soramitsu                          = ${ext}
    soramitsu.projectGroup             = ${ext?.projectGroup}
    soramitsu.docker                   = ${ext?.docker}
    soramitsu.docker.jar               = ${ext?.docker?.jar}
    soramitsu.docker.tag               = ${ext?.docker?.tag}
    soramitsu.docker.registry          = ${ext?.docker?.registry}
    soramitsu.docker.registry.url      = ${ext?.docker?.registry?.url}
    soramitsu.docker.registry.username = ${ext?.docker?.registry?.username}
    soramitsu.docker.registry.password = ${ext?.docker?.registry?.password}
    soramitsu.docker.registry.email    = ${ext?.docker?.registry?.email}
""")
            }
        }

    }
}
