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

    @Unroll
    def "[#projectName] copies custom files"() {
        given:
        def result
        def prjdir = new File("./projects/${projectName}")
        def build = new File(prjdir, 'build')
        def docker = new File(build, 'docker')
        def project = new GradleProjectExecutor(prjdir)

        when:
        result = project.runTask(SoraTask.dockerCopyFiles)

        then:
        taskSucceeded(result, SoraTask.dockerCopyFiles)
        result.output.contains("mapping")
        docker.exists()
        docker.list().contains("1")
        docker.list().contains("build.gradle")
        new File(docker, "1").list().contains("2")
        new File(docker, "1").list().contains("settings.gradle")
        new File(docker, "1/2").list().contains("gradlew")

        where:
        projectName = '01-java-app'
    }
}
