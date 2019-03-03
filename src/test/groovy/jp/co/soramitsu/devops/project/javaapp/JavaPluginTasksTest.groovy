package jp.co.soramitsu.devops.project.javaapp

import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification

class JavaPluginTasksTest extends Specification {

    def "only correct tasks runned"() {
        given: "a project"
        def result
        def project = new GradleProjectExecutor(new File("./projects/${projectName}"))

        when: "execute build task"
        result = project.runTask("build")
        println(result.output)

        then: "no test/check is executed"
        result.output.contains(projectName)
        taskSucceeded(result, "build")
        taskNotRunned(result, "test")
        taskNotRunned(result, "check")

        when: "execute test task"
        result = project.runTask("test")
        println(result.output)

        then: "no check is executed"
        result.output.contains(projectName)
        taskSucceeded(result, "build")
        taskSucceeded(result, "test")
        taskNotRunned(result, "check")

        when: "execute check task"
        result = project.runTask("check")
        println(result.output)

        then:
        taskSucceeded(result, "build")
        taskSucceeded(result, "check")
        taskSucceeded(result, "test")

        where:
        projectName << ['01-java-app']
    }

    boolean taskSucceeded(BuildResult result, String task) {
        return result.task(":${task}").outcome in [TaskOutcome.UP_TO_DATE, TaskOutcome.SUCCESS]
    }

    boolean taskNotRunned(BuildResult result, String task) {
        return result.task(":${task}") == null
    }
}
