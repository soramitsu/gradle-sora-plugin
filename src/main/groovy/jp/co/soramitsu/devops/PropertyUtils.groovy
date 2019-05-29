package jp.co.soramitsu.devops

import org.gradle.api.Project

class PropertyUtils {

    static final Optional<Object> getEnv(String name) {
        return Optional.ofNullable(System.getenv(name))
    }

    static final Optional<Object> getProjectProperty(Project project, String name) {
        try {
            return Optional.ofNullable(project.property(name))
        } catch (MissingPropertyException ignored) {
            return Optional.empty()
        }
    }

    static final Optional<Object> getSystemProperty(String name) {
        return Optional.ofNullable(System.getProperty(name))
    }

    static final Optional<Object> resolveProperty(Project project, String env, String prop) {
        return getEnv(env) ?:
                getSystemProperty(prop) ?:
                        getProjectProperty(project, prop)
    }
}
