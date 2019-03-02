package jp.co.soramitsu.devops

import jp.co.soramitsu.devops.task.PingTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class SoraPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task("ping", type: PingTask)
    }
}
