package jp.co.soramitsu.devops.docker

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerPlugin implements Plugin<Project> {

    static final String DOCKER_TASK_GROUP = "docker"

    @Override
    void apply(Project project) {
        project.pluginManager.apply(DockerRemoteApiPlugin.class)
    }
}
