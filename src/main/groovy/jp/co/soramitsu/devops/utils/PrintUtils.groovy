package jp.co.soramitsu.devops.utils

import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem

class PrintUtils {

    static void printBanner(final Project project) {
        def v = project.versionDetails()
        println("""
###############################################
 \t\t SORAMITSU PLUGIN
###############################################

  Project group:      ${project.group} 
  Project name:       ${project.name}
  Project version:    ${project.version}
  System:             ${OperatingSystem.current()}
  Version:            
    Last tag:         ${v.lastTag}
    CommitDistance:   ${v.commitDistance}
    Hash:             ${v.gitHash}
    Branch:           ${v.branchName}
    IsDirty:          ${!v.isCleanTag}

        """)
    }
}
