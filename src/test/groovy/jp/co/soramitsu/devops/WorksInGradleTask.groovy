package jp.co.soramitsu.devops

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll


class WorksInGradleTask extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    File settingsFile
    File buildFile

    def setup() {
        settingsFile = testProjectDir.newFile('settings.gradle')
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Unroll
    def "ping task returns pong for gradle-#gradleVersion"() {
        given:
        buildFile << """
            plugins {
                id 'soramitsu'
            }
        """

        when:
        def result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withArguments('ping')
                .withPluginClasspath()
                .build()

        then:
        result.output.contains('pong')
        result.task(":ping").outcome in [TaskOutcome.SUCCESS, TaskOutcome.UP_TO_DATE]

        where:
        gradleVersion << ['4.10', '5.2'
//                '4.0', '4.1', '4.2', '4.3', '4.4', '4.5', '4.6', '4.7', '4.8', '4.9', '4.10',
//                '5.0', '5.1', '5.2'
        ]
    }
}
