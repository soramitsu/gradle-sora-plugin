package jp.co.soramitsu.devops.docker

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.DockerVersion
import com.bmuschko.gradle.docker.tasks.RegistryCredentialsAware
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.SoramitsuExtension
import org.eclipse.jgit.annotations.NonNull
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.Copy

import java.util.stream.Collectors

import static jp.co.soramitsu.devops.utils.PrintUtils.format

class DockerPlugin implements Plugin<Project> {

    static final String DOCKER_TASK_GROUP = "docker"

    @Override
    void apply(Project project) {

        SoramitsuExtension ext = project.extensions.getByType(SoramitsuExtension)

        def dockerConfig = configureExtension(project, "docker", DockerConfig, ext)
        def registry = configureExtension(project, "registry", DockerRegistryConfig, dockerConfig)

        project.afterEvaluate { Project p ->
            project.pluginManager.apply(DockerRemoteApiPlugin.class)

            def tag = getDefaultTag(project, registry)
            setupDockerVersionTask(p)
            setupDockerCleanTask(p)
            setupDockerfileCreateTask(p, dockerConfig.jar)
            setupDockerCopyJarTask(p, dockerConfig.jar)
            setupDockerBuildTask(p, tag)
            setupDockerPushTask(p, registry, tag)
        }
    }

    static <T, E> T configureExtension(Project project, String name, Class<T> type, E ext) {
        return ((ExtensionAware) ext).extensions.create(name, type, project)
    }


    static String getDefaultTag(Project project, DockerRegistryConfig registry) {
        def parts = []
        parts << registry?.url
        parts << project.extensions.getByType(SoramitsuExtension)?.projectGroup
        parts << project.name

        parts = parts.stream()
                .filter({ p -> p != null })
                .collect(Collectors.toList())

        return Sanitize.tag("${parts.join("/")}:${project.version}")

    }

    static void setupDockerVersionTask(Project project) {
        project.tasks.register(SoraTask.dockerVersion, DockerVersion).configure { DockerVersion t ->
            t.group = DOCKER_TASK_GROUP
            t.description = "Print docker version"
        }
    }

    static void setupDockerPushTask(Project project, DockerRegistryConfig registry, String tag) {
        def errorMessageHeader = "Task ${SoraTask.dockerPush} is not available."
        if (registry == null) {
            println(format("$errorMessageHeader Define docker registry."))
            return
        } else if (registry.url == null) {
            println(format("$errorMessageHeader Define docker registry 'url' param."))
            return
        } else if (registry.username == null) {
            println(format("$errorMessageHeader Define docker registry 'username' param."))
            return
        } else if (registry.password == null) {
            println(format("$errorMessageHeader Define docker registry 'password' param."))
            return
        }

        project.tasks.withType(RegistryCredentialsAware).configureEach { RegistryCredentialsAware t ->
            t.registryCredentials.url.set registry.url
            t.registryCredentials.username.set registry.username
            t.registryCredentials.password.set registry.password
            t.registryCredentials.email.set registry.email
        }

        project.tasks.register(SoraTask.dockerPush, DockerPushImage).configure { DockerPushImage t ->
            t.group = DOCKER_TASK_GROUP
            t.description = "Push docker image to ${registry.url}"

            t.dependsOn([
                    SoraTask.dockerBuild,
            ])

            t.imageName.set(tag)
        }
    }

    static void setupDockerBuildTask(Project project, String tag) {
        project.tasks.register(SoraTask.dockerBuild, DockerBuildImage).configure { DockerBuildImage t ->
            t.group = DOCKER_TASK_GROUP
            t.description = "Build docker image with tag ${tag}"

            t.dependsOn([
                    SoraTask.dockerClean,
                    SoraTask.build,
                    SoraTask.dockerCopyJar,
                    SoraTask.dockerfileCreate
            ])

            t.tags.set([tag])

            t.inputDir.set getDockerContextDir(project)

            t.doLast {
                println(format("Built docker image with tags ${tag}"))
            }
        }
    }

    static void setupDockerCopyJarTask(Project project, @NonNull File jar) {
        project.tasks.register(SoraTask.dockerCopyJar, Copy).configure { Copy t ->
            t.group = DOCKER_TASK_GROUP
            t.description = "Copy jar file to ${getDockerContextDir(project).path}"
            t.dependsOn(SoraTask.build)

            println("JAR: ${jar.path}")
            t.from(jar)
            t.into(getDockerContextDir(project))
        }
    }

    static void setupDockerfileCreateTask(Project project, @NonNull File jar) {
        project.tasks.register(SoraTask.dockerfileCreate, Dockerfile).configure { Dockerfile t ->
            t.group = DOCKER_TASK_GROUP
            t.description = "Creates dockerfile in ${getDockerContextDir(project).path}"
            t.dependsOn(SoraTask.dockerCopyJar)

            t.from 'openjdk:8u191-jre-alpine'
            t.label([
                    "version"     : "${project.version}",
                    "built-date"  : "${new Date()}",
                    "built-by"    : "${System.getProperty('user.name')}",
                    "built-jdk"   : "${System.getProperty('java.version')}",
                    "built-gradle": "${project.gradle.gradleVersion}"
            ])
            t.instruction "MAINTAINER Bogdan Vaneev <bogdan@soramitsu.co.jp>"
            t.instruction "ARG MAX_RAM_FRACTION=4"
            t.instruction """ARG JAVA_OPTIONS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap \\
            -XX:MaxRAMFraction=\${MAX_RAM_FRACTION} -XX:+UseContainerSupport \\
            -XX:+PrintFlagsFinal -XshowSettings:vm -version \${JAVA_OPTIONS}"
            """
            t.copyFile jar.name, "/${jar.name}"
            t.defaultCommand 'java', '${JAVA_OPTIONS}', '-Djava.security.egd=file:/dev/./urandom', '-jar', "/${jar.name}"
        }
    }

    static void setupDockerCleanTask(Project project) {
        project.tasks.register(SoraTask.dockerClean).configure { Task t ->
            t.group = DOCKER_TASK_GROUP
            t.description = "Clean docker context dir"

            getDockerContextDir(project).deleteDir()
        }
    }

    static File getDockerContextDir(Project project) {
        return project.file("${project.buildDir}/docker")
    }
}
