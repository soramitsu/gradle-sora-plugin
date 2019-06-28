package jp.co.soramitsu.devops.tasks

import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import spock.lang.Specification

import static jp.co.soramitsu.devops.utils.TestUtils.taskSucceeded

class CustomFilesTaskTest extends Specification {

    def "[#projectName] app has custom file copy done"() {
        given: "gradle project"
        def result
        def td = new File("build/_files_test")
        td.mkdirs()

        def project = new GradleProjectExecutor(td)
        project.clearProject()
        def tag = "test"
        def jar = "jar.jar"
        def regUrl = "reg-url"
        def regUsername = "reg-username"
        def regPassword = "reg-password"
        def customFiles = ["\"file\"": "\"${project.buildFile.path}\""]

        when: "execute 'gradle tasks'"
        project.buildFile << """
            plugins {
                id '${TestUtils.PLUGIN_ID}'   
                id 'application'   
            }
            group = 'group'
            
            soramitsu {
              docker {
                tag = '${tag}'
                jar = new File('${jar}')
                customFiles = ${customFiles.toMapString()}
                registry {
                  url = '${regUrl}'
                  username = '${regUsername}'
                  password = '${regPassword}'
                }
              }
            }
        """
        result = project.runTask(SoraTask.dockerCopyFiles)
        println(result.output)

        then: "has completed custom file copy task"
        taskSucceeded(result, SoraTask.dockerCopyFiles)
        new File(project.buildFile.path).exists()

        where:
        projectName << "testproject"
    }
}
