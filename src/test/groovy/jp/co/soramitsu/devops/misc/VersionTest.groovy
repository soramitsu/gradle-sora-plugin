package jp.co.soramitsu.devops.misc

import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import org.gradle.testkit.runner.UnexpectedBuildFailure
import spock.lang.Specification


class VersionTest extends Specification {

    def "version must not be specified manually"() {
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
            group = 'group'
        """
        project.runTask("tasks")

        then: "no exception"
        noExceptionThrown()

        when: "add version manually"
        project.buildFile << "version = 1;"

        and:
        project.runTask("tasks")

        then: "build failed"
        thrown(UnexpectedBuildFailure.class)
    }

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
