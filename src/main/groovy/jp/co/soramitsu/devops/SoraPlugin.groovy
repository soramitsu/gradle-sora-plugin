package jp.co.soramitsu.devops


import jp.co.soramitsu.devops.coverage.CoveragePlugin
import jp.co.soramitsu.devops.docker.DockerConfig
import jp.co.soramitsu.devops.docker.DockerPlugin
import jp.co.soramitsu.devops.docker.DockerRegistryConfig
import jp.co.soramitsu.devops.misc.CustomJavaPlugin
import jp.co.soramitsu.devops.misc.InfoPlugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin

import static jp.co.soramitsu.devops.utils.PrintUtils.format

class SoraPlugin implements Plugin<Project> {

    void apply(Project project) {
        def soramitsu = project.extensions.create("soramitsu", SoramitsuExtension, project)
        def dockerConfig = soramitsu.extensions.create("docker", DockerConfig, project)
        def registry = dockerConfig.extensions.create("registry", DockerRegistryConfig, project)

        checkRequirements(project)
        setupRepositories(project)

        project.pluginManager.apply(CoveragePlugin.class)
        project.pluginManager.apply(CustomJavaPlugin.class)

        // if it is an application, then apply docker plugin
        project.plugins.withType(ApplicationPlugin, { ApplicationPlugin p ->
            project.pluginManager.apply(DockerPlugin.class)
        })

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

            abortIfHasUnwantedPlugins(project)

            try {
                // plugin may throw if it can not find .git directory
                project.pluginManager.apply(InfoPlugin.class)
            } catch (Exception e) {
                println(format("Git plugin thrown exception. Auto versioning will not work. Details ${e.toString()}"))
            }
        }
    }

    static void abortIfHasUnwantedPlugins(Project project) {
        def unwanted = []

        [
                'com.palantir.docker',
                'com.liferay.app.docker',
                'nebula.docker',
                'org.xbib.gradle.plugin.docker',
                'com.bmuschko.docker-java-application',
                'com.bmuschko.docker-spring-boot-application',
                'com.bmuschko.docker-remote-api',
                'com.google.cloud.tools.jib'
        ].each { id ->
            if (project.plugins.hasPlugin(id)) {
                unwanted << id
            }
        }

        if (!unwanted.empty) {
            throw new IllegalStateException(format("Please, remove the following plugins: ${unwanted}"))
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
