package jp.co.soramitsu.devops.docker

import groovy.transform.ToString
import org.gradle.api.Project

import static jp.co.soramitsu.devops.PropertyUtils.getEnv

@ToString
class DockerRegistryConfig {
    String url
    String username
    String password
    String email

    private Project project

    DockerRegistryConfig(Project project) {
        this.project = project

        project.afterEvaluate { Project p ->
            url = getEnv("DOCKER_REGISTRY_URL").orElse(url) as String
            username = getEnv("DOCKER_REGISTRY_USERNAME").orElse(username) as String
            password = getEnv("DOCKER_REGISTRY_PASSWORD").orElse(password) as String
            email = getEnv("DOCKER_REGISTRY_EMAIL").orElse(email) as String
        }

    }
}
