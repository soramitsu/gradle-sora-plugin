package jp.co.soramitsu.devops.tasks

import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import org.gradle.testkit.runner.BuildResult
import spock.lang.Specification

class TasksTest extends Specification {

    def "has required gradle tasks"() {
        given:
        def result
        def project = new GradleProjectExecutor(new File("./projects/${projectName}"))

        when:
        result = project.runTask("tasks")
        println(result.output)

        then:
        hasTask(result, 'build')
        hasTask(result, 'test')
        hasTask(result, 'check')
        hasTask(result, 'jacocoTestReport')
        hasTask(result, 'printVersion')
        hasTask(result, 'osInfo')

        where:
        projectName << ['01-java-app']
    }

    boolean hasTask(BuildResult result, String taskName){
        return result.output.contains(taskName)
    }
}
