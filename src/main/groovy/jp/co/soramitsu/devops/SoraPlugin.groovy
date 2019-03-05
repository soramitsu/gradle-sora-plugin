package jp.co.soramitsu.devops

import jp.co.soramitsu.devops.coverage.CoveragePlugin
import jp.co.soramitsu.devops.docker.DockerPlugin
import jp.co.soramitsu.devops.misc.CustomJavaPlugin
import jp.co.soramitsu.devops.misc.InfoPlugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

import static jp.co.soramitsu.devops.utils.PrintUtils.format

class SoraPlugin implements Plugin<Project> {

    static final String SORAMITSU_EXTENSION_NAME = "soramitsu"

    void apply(Project project) {
        project.extensions.create(SORAMITSU_EXTENSION_NAME, SoramitsuExtension)

        checkRequirements(project)
        setupRepositories(project)

        project.pluginManager.apply(CoveragePlugin.class)
        project.pluginManager.apply(CustomJavaPlugin.class)
        project.pluginManager.apply(DockerPlugin.class)

    }

    static void doForSpringApp(Project project, Action<? super Plugin> action) {
        project.plugins.withId('org.springframework.boot', action)
    }

    static void checkRequirements(Project project) {
        project.afterEvaluate { Project p ->
            if (p.version != null && "unspecified" != p.version && !p.version.toString().empty) {
                throw new IllegalStateException(format("Please, remove line with 'version' from build.gradle: 'version = ${project.version}'"))
            }

            if (p.group == null || p.group.toString().empty) {
                throw new IllegalStateException(format("Please, specify 'group'"))
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
