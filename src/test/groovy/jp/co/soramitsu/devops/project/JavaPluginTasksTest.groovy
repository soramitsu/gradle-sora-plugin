package jp.co.soramitsu.devops.project

import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import spock.lang.Specification
import spock.lang.Unroll

import static jp.co.soramitsu.devops.utils.TestUtils.taskNotRunned
import static jp.co.soramitsu.devops.utils.TestUtils.taskSucceeded

class JavaPluginTasksTest extends Specification {

    @Unroll
    def "[#projectName] only correct tasks runned"() {
        given: "a project"
        def result
        def project = new GradleProjectExecutor(new File("./projects/${projectName}"))

        when: "execute build task"
        result = project.runTask("build")
        println(result.output)

        then: "no test/check is executed"
        result.output.contains(projectName)
        taskSucceeded(result, "build")
        taskSucceeded(result, "test")
        taskSucceeded(result, "check")

        where:
        projectName << TestUtils.projects
    }

}
