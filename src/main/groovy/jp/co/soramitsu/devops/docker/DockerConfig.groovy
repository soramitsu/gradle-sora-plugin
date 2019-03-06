package jp.co.soramitsu.devops.docker

import groovy.transform.ToString
import jp.co.soramitsu.devops.utils.PrintUtils
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

@ToString
class DockerConfig {

    File jar

    Iterable<String> tags

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
        project.tasks.withType(Jar).configureEach { Jar t ->
            // check if we produce exactly one jar
            assert t.outputs.files.files.size() == 1, "**ERROR**: Should be 1 JAR, got: ${t.outputs.files.files}"

            // jar = file == null ? file : null
            jar = t?.archivePath
        }

        project.afterEvaluate {
            if (jar == null) {
                throw new IllegalStateException(PrintUtils.format("Can not find JAR. Specify soramitsu.docker.jar property"))
            }
        }
    }

}
