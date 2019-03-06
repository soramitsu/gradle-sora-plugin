package jp.co.soramitsu.devops.project

import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import spock.lang.Specification
import spock.lang.Unroll

import static jp.co.soramitsu.devops.utils.TestUtils.taskSucceeded

class DockerTasksTest extends Specification {

    @Unroll
    def "[#projectName] dockerBuild succeeds"() {
        given:
        def result
        def project = new GradleProjectExecutor(new File("./projects/${projectName}"))

        when:
        result = project.runTask(SoraTask.dockerBuild)

        then:
        taskSucceeded(result, SoraTask.dockerBuild)

        where:
        projectName << TestUtils.apps
    }
}
