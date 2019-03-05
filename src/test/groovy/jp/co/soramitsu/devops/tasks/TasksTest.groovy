package jp.co.soramitsu.devops.tasks

import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import org.gradle.testkit.runner.BuildResult
import spock.lang.Specification

class TasksTest extends Specification {

    def "has required gradle tasks"() {
        given: "gradle project"
        def result
        def project = new GradleProjectExecutor(new File("./projects/${projectName}"))

        when: "execute 'gradle tasks'"
        result = project.runTask("tasks")
        println(result.output)

        then: "has this tasks"
        hasTask(result, SoraTask.build)
        hasTask(result, SoraTask.test)
        hasTask(result, SoraTask.check)
        hasTask(result, SoraTask.coverage)
        hasTask(result, SoraTask.printVersion)
        hasTask(result, SoraTask.printOsInfo)
        hasTask(result, SoraTask.dockerCopyJar)
        hasTask(result, SoraTask.dockerfileCreate)
        hasTask(result, SoraTask.dockerBuild)
        hasTask(result, SoraTask.dockerClean)
        hasTask(result, SoraTask.dockerPush)
        hasTask(result, SoraTask.dockerVersion)

        and: "has no this tasks"
        !hasTask(result, 'jacocoTestReport')
        !hasTask(result, 'jacocoTestCoverageVerification')

        where:
        projectName << TestUtils.projects
    }

    boolean hasTask(BuildResult result, String taskName) {
        return result.output.contains(taskName)
    }
}
