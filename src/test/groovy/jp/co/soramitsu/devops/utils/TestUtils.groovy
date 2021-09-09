package jp.co.soramitsu.devops.utils

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification


class TestUtils extends Specification {

    static final String PLUGIN_ID = "jp.co.soramitsu.sora-plugin"

    static final def apps = [
            '01-java-app',
            '03-kotlin-app',
            '05-java-spring-app'
    ]

    static final def libs = [
            '02-java-lib',
            '04-kotlin-lib',
    ]

    static final def projects = [
            apps, libs
    ].flatten()

    static boolean taskSucceeded(BuildResult result, String task) {
        return result.task(":${task}").outcome in [TaskOutcome.UP_TO_DATE, TaskOutcome.SUCCESS, TaskOutcome.NO_SOURCE]
    }

    static boolean taskNotRan(BuildResult result, String task) {
        return result.task(":${task}") == null
    }
}
