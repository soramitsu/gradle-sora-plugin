package jp.co.soramitsu.devops.docker

import groovy.transform.ToString
import org.gradle.api.Project

@ToString
class DockerConfig {

    File jar

    String tag

    DockerRegistryConfig registry

    DockerRegistryConfig registry(Closure closure) {
        registry = new DockerRegistryConfig(this.project)
        project.configure(registry, closure)
        return registry
    }

    private Project project

    DockerConfig(Project project) {
        this.project = project
        configureJarPath(project)
    }

    void configureJarPath(Project project) {
        project.afterEvaluate {
            if (jar == null) {
                jar = project.jar.archiveFile.get().asFile
            }
        }
    }

}
