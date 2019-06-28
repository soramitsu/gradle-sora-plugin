package jp.co.soramitsu.devops.docker

import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

@ToString
@CompileStatic
class DockerConfig {

    @InputFile
    File jar

    @Input
    @Optional
    String tag

    @Input
    @Optional
    String version

    @Input
    @Optional
    Map<String, String> customFiles

    @Input
    @Optional
    String[] args

    private Project project

    DockerConfig(Project project) {
        this.project = project
    }
}
