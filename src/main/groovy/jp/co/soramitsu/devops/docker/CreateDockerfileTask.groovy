package jp.co.soramitsu.devops.docker

import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class CreateDockerfileTask extends Dockerfile {

    DockerConfig config

    @Inject
    CreateDockerfileTask(DockerConfig config) {
        this.config = config
    }
    /**
     * FROM openjdk:8u191-jre-alpine
     *
     * ARG JAR_FILE
     * # set 1/4 of available RAM for JVM
     * ARG MAX_RAM_FRACTION=4
     * ARG JAVA_OPTIONS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap \
     *   -XX:MaxRAMFraction=${MAX_RAM_FRACTION} -XX:+UseContainerSupport \
     *   -XX:+PrintFlagsFinal -XshowSettings:vm -version ${JAVA_OPTIONS}"
     * COPY ${JAR_FILE} /app.jar
     *
     * ENTRYPOINT ["java", "${JAVA_OPTIONS}", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
     * @param project
     */
    @TaskAction
    void apply(Project project) {
        from('openjdk:8u191-jre-alpine')
        instruction("ARG MAX_RAM_FRACTION=4")
        instruction("""ARG JAVA_OPTIONS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap \\
            -XX:MaxRAMFraction=\${MAX_RAM_FRACTION} -XX:+UseContainerSupport \\
            -XX:+PrintFlagsFinal -XshowSettings:vm -version \${JAVA_OPTIONS}"
        """)
        copyFile(config.jar.path, '/app.jar')
        defaultCommand('java', '${JAVA_OPTIONS}', '-Djava.security.egd=file:/dev/./urandom', '-jar', '/app.jar')
    }
}
