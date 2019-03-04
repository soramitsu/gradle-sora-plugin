package jp.co.soramitsu.devops


import jp.co.soramitsu.devops.coverage.CoveragePlugin
import jp.co.soramitsu.devops.docker.DockerPlugin
import jp.co.soramitsu.devops.misc.CustomJavaPlugin
import jp.co.soramitsu.devops.misc.InfoPlugin
import jp.co.soramitsu.devops.utils.PrintUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class SoraPlugin implements Plugin<Project> {

    void apply(Project project) {
        checkRequirements(project)
        setupRepositories(project)

        project.pluginManager.apply(CoveragePlugin.class)
        project.pluginManager.apply(CustomJavaPlugin.class)
        project.pluginManager.apply(DockerPlugin.class)

        SoramitsuConfig c = project.extensions.create("soramitsu", SoramitsuConfig)
    }

    static void checkRequirements(Project project) {
        project.afterEvaluate { Project p ->
            if (p.version != null && "unspecified" != p.version) {
                throw new IllegalStateException(PrintUtils.format("Please, remove line with 'version' from build.gradle: 'version = ${project.version}'"))
            }

            if (p.group == null || p.group.toString().empty) {
                throw new IllegalStateException(PrintUtils.format("Please, specify 'group'"))
            }

            project.pluginManager.apply(InfoPlugin.class)
        }
    }

    static void setupRepositories(Project project) {
        project.repositories.addAll([
                project.repositories.maven {
                    url 'https://jitpack.io'
                },
                project.repositories.maven {
                    url 'https://plugins.gradle.org/m2/'
                },
                project.repositories.jcenter(),
                project.repositories.gradlePluginPortal(),
                project.repositories.mavenCentral()
        ])
    }
}
