package jp.co.soramitsu.devops.utils

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

class BaseIntegrationTest extends Specification {
    private static final String defaultGradleVersion = '8.4'

    @TempDir
    Path testProjectDir
    protected File settingsFile
    protected File buildFile

    def setup() {
        settingsFile = testProjectDir.newFile('settings.gradle')
        buildFile = testProjectDir.newFile('build.gradle')
    }

    BuildResult runTask(String taskName) {
        return runTask(taskName, defaultGradleVersion)
    }

    BuildResult runTask(String taskName, String gradleVersion) {
        return GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root.toFile())
                .withArguments(taskName)
                .withPluginClasspath()
                .build()
    }
}
