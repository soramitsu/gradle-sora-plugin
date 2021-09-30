package jp.co.soramitsu.devops.utils

import org.apache.commons.io.FileUtils
import org.gradle.internal.impldep.org.apache.commons.lang.RandomStringUtils
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class GradleProjectExecutor {

    Logger logger = LoggerFactory.getLogger(GradleProjectExecutor.class)

    private static final String defaultGradleVersion = '7.1.1'

    File projectDir
    protected File buildFile
    protected File settingsFile

    GradleProjectExecutor(String projectName) {
        var soraPluginDir = File.createTempDir()
        FileUtils.copyDirectory(new File("./"), soraPluginDir)
        projectDir = new File("${soraPluginDir.toString()}/projects/${projectName}")

        if (projectDir == null) {
            throw new IllegalStateException("specify projectDir")
        }
        if (!projectDir.exists()) {
            throw new IllegalArgumentException("Project dir does not exist: ${projectDir.canonicalPath}")
        }
        if (!projectDir.isDirectory()) {
            throw new IllegalArgumentException("Project dir is not a directory: ${projectDir.canonicalPath}")
        }

        settingsFile = new File(projectDir, 'settings.gradle')
        buildFile = new File(projectDir, 'build.gradle')

        var enabledTasks = "\ntasks.register(\"enabledTasks\") { task -> println(tasks.stream().filter { it.enabled && it.name != task.name }.map { it.name }.toList()) }"
        buildFile.append(enabledTasks)
    }

    void clearProject() {
        settingsFile.delete()
        settingsFile.createNewFile()
        buildFile.delete()
        buildFile.createNewFile()

        settingsFile << "rootProject.name = 'sora-test-${RandomStringUtils.randomAlphabetic(5)}'"
    }

    BuildResult runTask(String taskName) {
        return runTask(taskName, defaultGradleVersion)
    }

    BuildResult runTask(String taskName, Map<String, String> env) {
        def gradleVersion = defaultGradleVersion
        logger.warn("running task :${taskName} with gradle-${gradleVersion} in ${projectDir.path}")
        return GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withEnvironment(env)
                .withProjectDir(projectDir)
                .withArguments(taskName)
                .withPluginClasspath()
                .forwardOutput()
                .build()
    }

    BuildResult runTask(String taskName, String gradleVersion) {
        logger.warn("running task :${taskName} with gradle-${gradleVersion} in ${projectDir.path}")
        return GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(projectDir)
                .withArguments(taskName)
                .withPluginClasspath()
                .forwardOutput()
                .build()
    }
}
