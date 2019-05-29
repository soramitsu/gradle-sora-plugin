package jp.co.soramitsu.devops

import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.gradle.api.Project

@ToString
@CompileStatic
class SoramitsuExtension {

    String projectGroup

    private Project project

    SoramitsuExtension(Project project) {
        this.project = project
    }
}
