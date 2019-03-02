package jp.co.soramitsu.devops.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PingTask extends DefaultTask {

    @TaskAction
    def action(){
        println("pong")
    }
}
