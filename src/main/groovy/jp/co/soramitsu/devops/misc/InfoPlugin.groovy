package jp.co.soramitsu.devops.misc

import com.palantir.gradle.gitversion.GitVersionPlugin
import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.SoramitsuExtension
import jp.co.soramitsu.devops.docker.DockerConfig
import jp.co.soramitsu.devops.docker.DockerRegistryConfig
import jp.co.soramitsu.devops.utils.PrintUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import java.util.stream.Collectors

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

            def ext = project.extensions.getByType(SoramitsuExtension)
            def docker = ext.extensions.getByType(DockerConfig)
            def registry = docker.extensions.getByType(DockerRegistryConfig)
            t.doLast {
                println("""
    soramitsu                          = ${ext}
    soramitsu.projectGroup             = ${ext?.projectGroup}
    soramitsu.docker                   = ${docker}
    soramitsu.docker.jar               = ${docker?.jar}
    soramitsu.docker.tag               = ${docker?.tag}
    soramitsu.docker.registry          = ${registry}
    soramitsu.docker.registry.url      = ${registry?.url}
    soramitsu.docker.registry.username = ${registry?.username}
    soramitsu.docker.registry.password = ${registry?.password}
    soramitsu.docker.registry.email    = ${registry?.email}
""")
            }
        }

        project.tasks.register(SoraTask.printDockerImage).configure { Task t ->
            t.group = INFO_GROUP_NAME
            t.description = "Prints Docker Image that will be used in dockerPush"

            def ext = project.extensions.getByType(SoramitsuExtension)
            def dockerConfig = ext.extensions.getByType(DockerConfig)
            def registry = dockerConfig.extensions.getByType(DockerRegistryConfig)

            def parts = []
            parts << registry?.url
            parts << project.extensions.getByType(SoramitsuExtension)?.projectGroup
            parts << project.name

            parts = parts.stream()
                    .filter({ p -> p != null })
                    .collect(Collectors.joining("/"))
            def tag = dockerConfig.tag
            if (tag == null) {
                tag = project.version
            }

            t.doLast {
                println("""
    docker.registry      = ${registry?.url}
    docker.projectGroup  = ${project.extensions.getByType(SoramitsuExtension)?.projectGroup}
    docker.projectName   = ${project.name}
    docker.imageName     = ${parts}
    docker.tag           = ${tag}
    docker.fullImageName = ${parts}:${tag}
""")
            }
        }

    }
}
