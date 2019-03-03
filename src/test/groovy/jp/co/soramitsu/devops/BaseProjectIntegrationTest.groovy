package jp.co.soramitsu.devops


import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class BaseProjectIntegrationTest {

    Logger logger = LoggerFactory.getLogger(BaseProjectIntegrationTest.class)

    private static final String defaultGradleVersion = '4.10'

    File projectDir

    BaseProjectIntegrationTest(File dir) {
        this.projectDir = dir

        if (projectDir == null) {
            throw new IllegalStateException("specify projectDir")
        }

        if (!projectDir.exists()) {
            throw new IllegalArgumentException("Project dir does not exist: ${projectDir.canonicalPath}")
        }

        if (!projectDir.isDirectory()) {
            throw new IllegalArgumentException("Project dir is not a directory: ${projectDir.canonicalPath}")
        }
    }

    BuildResult runTask(String taskName) {
        return runTask(taskName, defaultGradleVersion)
    }

    BuildResult runTask(String taskName, String gradleVersion) {
        logger.warn("running task :${taskName} with gradle-${gradleVersion} in ${projectDir.path}")
        return GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(projectDir)
                .withArguments(taskName)
                .withPluginClasspath()
                .build()
    }


}
