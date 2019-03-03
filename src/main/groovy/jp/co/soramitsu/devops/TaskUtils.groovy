package jp.co.soramitsu.devops

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

class TaskUtils {
    static TaskProvider<Task> getTaskProvider(Project project, String taskName) {
        return project.getTasks().named(taskName)
    }
}
