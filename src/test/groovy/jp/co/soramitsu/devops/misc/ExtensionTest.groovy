package jp.co.soramitsu.devops.misc

import jp.co.soramitsu.devops.utils.GradleProjectExecutor
import jp.co.soramitsu.devops.utils.TestUtils
import spock.lang.Specification

import static jp.co.soramitsu.devops.utils.TestUtils.taskSucceeded

class ExtensionTest extends Specification {

    def "all fiels are filled for extension"() {
        given:
        def td = new File("build/_test")
        td.mkdirs()

        def project = new GradleProjectExecutor(td)
        project.clearProject()

        def projectGroup = "ext-projectGroup"
        def tag = "ext-tag"
        def jar = "ext-jar"
        def regUrl = "ext-reg-url"
        def regUsername = "ext-reg-username"
        def regPassword = "ext-reg-password"

        when: "build file has no version specified"
        project.buildFile << """
            plugins {
                id '${TestUtils.PLUGIN_ID}'   
            }
            group = 'group'
            
            soramitsu {
              projectGroup = '${projectGroup}'
              docker {
                tag = '${tag}'
                jar = new File('${jar}')
                registry {
                  url = '${regUrl}'
                  username = '${regUsername}'
                  password = '${regPassword}'
                }
              }
            }
        """
        def result = project.runTask("printConfig")

        then: "no exception"
        result
        result.output.contains("jp.co.soramitsu.devops.SoramitsuExtension")
        result.output.contains("jp.co.soramitsu.devops.docker.DockerConfig")
        result.output.contains("jp.co.soramitsu.devops.docker.DockerRegistryConfig")
        result.output.contains(projectGroup)
        result.output.contains(tag)
        result.output.contains(jar)
        result.output.contains(regUrl)
        result.output.contains(regUsername)
        result.output.contains(regPassword)
        taskSucceeded(result, "printConfig")
        noExceptionThrown()
    }

    def "registry is filled with env var"() {
        given:
        def td = new File("build/_test")
        td.mkdirs()

        def project = new GradleProjectExecutor(td)
        project.clearProject()

        def projectGroup = "ext-projectGroup"
        def tag = "ext-tag"
        def jar = "ext-jar"
        def regUrl = "ext-reg-url"
        def regUsername = "ext-reg-username"
        def regPassword = "ext-reg-password"

        when: "build file has no version specified"
        project.buildFile << """
            plugins {
                id '${TestUtils.PLUGIN_ID}'   
            }
            group = 'group'
            
            
            soramitsu {
              projectGroup = '${projectGroup}'
              docker {
                tag = '${tag}'
                jar = new File('${jar}')
                registry {}
              }
            }
        """
        def result = project.runTask("printConfig", [
                "DOCKER_REGISTRY_URL"     : regUrl,
                "DOCKER_REGISTRY_USERNAME": regUsername,
                "DOCKER_REGISTRY_PASSWORD": regPassword,
        ])

        then: "no exception"
        result
        result.output.contains("jp.co.soramitsu.devops.SoramitsuExtension")
        result.output.contains("jp.co.soramitsu.devops.docker.DockerConfig")
        result.output.contains("jp.co.soramitsu.devops.docker.DockerRegistryConfig")
        result.output.contains(projectGroup)
        result.output.contains(tag)
        result.output.contains(jar)
        result.output.contains(regUrl)
        result.output.contains(regUsername)
        result.output.contains(regPassword)
        taskSucceeded(result, "printConfig")
        noExceptionThrown()
    }

    def "no default extension values"() {
        given:
        def td = new File("build/_test")
        td.mkdirs()

        def project = new GradleProjectExecutor(td)
        project.clearProject()

        when: "build file has no version specified"
        project.buildFile << """
            plugins {
                id '${TestUtils.PLUGIN_ID}'   
            }
            group = 'group'
            
        """
        def result = project.runTask("printConfig")

        then: "no exception"
        result
        result.output.contains("jp.co.soramitsu.devops.SoramitsuExtension")
        result.output.contains("jp.co.soramitsu.devops.docker.DockerConfig")
        result.output.contains("jp.co.soramitsu.devops.docker.DockerRegistryConfig")
        taskSucceeded(result, "printConfig")
        noExceptionThrown()
    }
}
