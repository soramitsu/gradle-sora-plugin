package jp.co.soramitsu.devops.project.javaapp

import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import spock.lang.Specification


class DockerTasksTest extends Specification {

    def "docker clean removes"() {
        given:
        def result
        def project = new GradleProjectExecutor(new File("./projects/${projectName}"))

        where:
        projectName << TestUtils.projects
    }
}
