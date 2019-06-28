package jp.co.soramitsu.devops.tasks


import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import spock.lang.Specification
import spock.lang.Unroll

class CustomFilesTaskTest extends Specification {

    @Unroll
    def "[#projectName] app has custom file copy done"() {
        given: "gradle project"
        def result
        def project = new GradleProjectExecutor(new File("./projects/${projectName}"))
        def tag = "test"
        def jar = "jar.jar"
        def customFiles = [file: project.buildFile]

        when: "execute 'gradle tasks'"
        project.buildFile << """
            plugins {
                id '${TestUtils.PLUGIN_ID}'   
            }
            group = 'group'
            
            soramitsu {
              docker {
                tag = '${tag}'
                jar = new File('${jar}')
                customFiles = $customFiles
              }
            }
        """
        result = project.runTask("tasks")
        println(result.output)

        then: "has completed custom file copy task"
        new File(project.projectDir.path + project.buildFile.path).exists()

        where:
        projectName << TestUtils.apps
    }
}
