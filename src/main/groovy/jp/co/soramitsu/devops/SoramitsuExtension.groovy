package jp.co.soramitsu.devops

import jp.co.soramitsu.devops.docker.DockerConfig
import org.gradle.api.Project

class SoramitsuExtension {

    String projectGroup

    DockerConfig docker

    def docker(Closure closure) {
        docker = new DockerConfig(this.project)
        project.configure(docker, closure)
        return docker
    }

    private Project project

    SoramitsuExtension(Project project) {
        this.project = project
    }

}
