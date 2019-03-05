package jp.co.soramitsu.devops.misc

import com.palantir.gradle.gitversion.GitVersionPlugin
import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.utils.PrintUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class InfoPlugin implements Plugin<Project> {

    static final String INFO_GROUP_NAME = "info"

    @Override
    void apply(Project project) {
        project.pluginManager.apply(GitVersionPlugin.class)

        // set project version based on git
        project.version = project.gitVersion()

        project.tasks.named(SoraTask.printVersion).configure { Task t ->
            t.group = INFO_GROUP_NAME
            t.description = "Print git version information"
        }

        project.tasks.register(SoraTask.printOsInfo).configure { Task t ->
            t.group = INFO_GROUP_NAME
            t.description = "Print OS and version information"
            t.doLast {
                PrintUtils.printBanner(project)
            }
        }
    }
}
