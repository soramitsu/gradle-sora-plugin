package jp.co.soramitsu.devops.tasks

import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import org.gradle.testkit.runner.BuildResult
import spock.lang.Specification
import spock.lang.Unroll

class TasksTest extends Specification {

    @Unroll
    def "[#projectName] app has required gradle tasks"() {
        given: "gradle project"
        def result
        def project = new GradleProjectExecutor(projectName)

        when: "execute 'gradle tasks'"
        result = project.runTask("taskList")
        println(result.output)

        then: "has this tasks"
        hasTask(result, SoraTask.build)
        hasTask(result, SoraTask.test)
        hasTask(result, SoraTask.check)
        hasTask(result, SoraTask.coverage)
        hasTask(result, SoraTask.printVersion)
        hasTask(result, SoraTask.printOsInfo)
        hasTask(result, SoraTask.dockerCopyJar)
        hasTask(result, SoraTask.dockerCopyFiles)
        hasTask(result, SoraTask.dockerfileCreate)
        hasTask(result, SoraTask.dockerBuild)
        hasTask(result, SoraTask.dockerClean)
        hasTask(result, SoraTask.dockerPush)
        hasTask(result, SoraTask.dockerVersion)

        and: "has disabled tasks"
        isTaskDisabled(project, 'jacocoTestReport')
        isTaskDisabled(project, 'jacocoTestCoverageVerification')

        where:
        projectName << TestUtils.apps
    }

    @Unroll
    def "[#projectName] lib has required gradle tasks"() {
        given: "gradle project"
        def result
        def project = new GradleProjectExecutor(projectName)

        when: "execute 'gradle tasks'"
        result = project.runTask("taskList")
        println(result.output)

        then: "has this tasks"
        hasTask(result, SoraTask.build)
        hasTask(result, SoraTask.test)
        hasTask(result, SoraTask.check)
        hasTask(result, SoraTask.coverage)
        hasTask(result, SoraTask.printVersion)
        hasTask(result, SoraTask.printOsInfo)

        and: "has no this tasks"
        !hasTask(result, SoraTask.dockerCopyJar)
        !hasTask(result, SoraTask.dockerCopyFiles)
        !hasTask(result, SoraTask.dockerfileCreate)
        !hasTask(result, SoraTask.dockerBuild)
        !hasTask(result, SoraTask.dockerClean)
        !hasTask(result, SoraTask.dockerPush)
        !hasTask(result, SoraTask.dockerVersion)

        and: "has disabled tasks"
        isTaskDisabled(project, 'jacocoTestReport')
        isTaskDisabled(project, 'jacocoTestCoverageVerification')

        where:
        projectName << TestUtils.libs
    }

    static boolean hasTask(BuildResult result, final String taskName) {
        return result.output.contains(taskName)
    }

    static boolean isTaskDisabled(GradleProjectExecutor project, final String name) {
        var result = project.runTask(name)
        return result.output.contains("Task :${name} SKIPPED")
    }
}
