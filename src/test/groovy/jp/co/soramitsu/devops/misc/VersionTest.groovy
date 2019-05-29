package jp.co.soramitsu.devops.misc

import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import org.gradle.testkit.runner.UnexpectedBuildFailure
import spock.lang.Specification


class VersionTest extends Specification {

    def "group must be specified"() {
        given:
        def td = new File("build/_test")
        td.mkdirs()

        def project = new GradleProjectExecutor(td)
        project.buildFile.delete()

        when: "build file has no version specified"
        project.buildFile << """
            plugins {
                id '${TestUtils.PLUGIN_ID}'   
            }
        """
        project.runTask("tasks")

        then:
        thrown(UnexpectedBuildFailure.class)

        when: "add group manually"
        project.buildFile << "group = 'group'"

        then: "no exception"
        noExceptionThrown()
    }
}
