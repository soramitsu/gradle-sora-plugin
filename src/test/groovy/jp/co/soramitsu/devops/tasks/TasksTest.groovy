package jp.co.soramitsu.devops.tasks

import jp.co.soramitsu.devops.base.GradleProjectExecutor
import spock.lang.Specification


class TasksTest extends Specification {

    def "gradle tasks"() {
        given:
        def result
        def project = new GradleProjectExecutor(new File("./projects/${projectName}"))

        when:
        result = project.runTask("tasks")
        println(result.output)

        then:
        true

        where:
        projectName << ['01-java-app']
    }
}
