package jp.co.soramitsu.devops.misc

import com.palantir.gradle.gitversion.GitVersionPlugin
import jp.co.soramitsu.devops.utils.PrintUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class InfoPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply(GitVersionPlugin.class)

        // set project version based on git
        project.version = project.gitVersion()

        project.tasks.named("printVersion").configure {
            group = "info"
            description = "Print git version information"
        }

        project.tasks.register("osInfo") {
            group = "info"
            description = "Print OS and version information"
            doLast {
                PrintUtils.printBanner(project)
            }
        }
    }
}
