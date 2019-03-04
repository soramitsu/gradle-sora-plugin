package jp.co.soramitsu.devops.docker

import com.bmuschko.gradle.docker.DockerRegistryCredentials
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile

class DockerConfig {

    @InputFile
    File jar

    @Input
    DockerRegistryCredentials credentials

}
