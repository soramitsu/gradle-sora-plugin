package jp.co.soramitsu.devops.docker

import com.bmuschko.gradle.docker.DockerRegistryCredentials
import groovy.transform.ToString
import jp.co.soramitsu.devops.SoramitsuExtension
import jp.co.soramitsu.devops.utils.PrintUtils
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

import java.util.stream.Collectors

import static jp.co.soramitsu.devops.PropertyUtils.resolveProperty

@ToString
class DockerConfig {

    File jar

    Iterable<String> tags

    DockerRegistryCredentials registryCredentials

    private Project project

    DockerConfig(Project project) {
        this.project = project
        this.registryCredentials = getDefaultRegistryCredentials()
        this.tags = getDefaultTags()
        configureJarPath(project)
    }

    Iterable<String> getDefaultTags() {
        def parts = []
        parts << registryCredentials.url?.orNull
        parts << project.extensions.getByType(SoramitsuExtension)?.projectGroup
        parts << project.name

        parts = parts.stream()
                .filter({ p -> p != null })
                .collect(Collectors.toList())


        return [
                Sanitize.tag("${parts.join("/")}:${project.version}")
        ]
    }

    DockerRegistryCredentials getDefaultRegistryCredentials() {
        DockerRegistryCredentials dcr = new DockerRegistryCredentials(project)

        dcr.url.set resolveProperty(project, "DOCKER_REGISTRY_URL", "soramitsu.docker.registry.url")
                .orElse(null) as String

        dcr.username.set resolveProperty(project, "DOCKER_REGISTRY_USERNAME", "soramitsu.docker.registry.username")
                .orElse(null) as String

        dcr.password.set resolveProperty(project, "DOCKER_REGISTRY_PASSWORD", "soramitsu.docker.registry.password")
                .orElse(null) as String

        dcr.email.set resolveProperty(project, "DOCKER_REGISTRY_EMAIL", "soramitsu.docker.registry.email")
                .orElse(null) as String

        return dcr
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
                throw new IllegalStateException(PrintUtils.format("Specify soramitsu.jar property"))
            }
        }
    }

}
