package jp.co.soramitsu.devops.docker

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.DockerVersion
import com.bmuschko.gradle.docker.tasks.RegistryCredentialsAware
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import jp.co.soramitsu.devops.SoraTask
import jp.co.soramitsu.devops.SoramitsuExtension
import jp.co.soramitsu.devops.utils.JavaUtils
import org.eclipse.jgit.annotations.NonNull
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy

import java.util.stream.Collectors

import static jp.co.soramitsu.devops.utils.PrintUtils.format

class DockerPlugin implements Plugin<Project> {

    static final String DOCKER_TASK_GROUP = "docker"

    @Override
    void apply(Project project) {
        project.afterEvaluate { Project p ->
            def ext = project.extensions.getByType(SoramitsuExtension)
            def dockerConfig = ext.extensions.getByType(DockerConfig)
            def registry = dockerConfig.extensions.getByType(DockerRegistryConfig)

            def jar = dockerConfig.jar
            if (jar == null) {
                println(format("soramitsu.docker.jar is null, no docker tasks available"))
                return
            }

            project.pluginManager.apply(DockerRemoteApiPlugin.class)

            def tag = getDefaultTag(project, registry, dockerConfig)
            setupDockerVersionTask(p)
            setupDockerCleanTask(p)
            setupDockerfileCreateTask(p, dockerConfig)
            setupDockerCopyJarTask(p, jar)
            setupDockerCopyFilesTask(p, dockerConfig)
            setupDockerBuildTask(p, tag)
            setupDockerPushTask(p, registry, tag)
        }
    }

    static String getDefaultTag(Project project,
                                DockerRegistryConfig registry,
                                DockerConfig dockerConfig) {
        def parts = []
        parts << registry?.url
        parts << project.extensions.getByType(SoramitsuExtension)?.projectGroup
        parts << project.name

        parts = parts.stream()
                .filter({ p -> p != null })
                .collect(Collectors.joining("/"))
        def tag = dockerConfig.tag
        if (tag == null) {
            tag = project.version
        }
        return Sanitize.tag("${parts}:$tag")

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

            t.images.set([tag])
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
                    SoraTask.dockerCopyFiles,
                    SoraTask.dockerfileCreate
            ])

            t.images.set([tag])

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

            println(format("JAR: ${jar.path}"))
            t.from(jar)
            t.into(getDockerContextDir(project))
        }
    }

    static void setupDockerCopyFilesTask(Project project, @NonNull DockerConfig dockerConfig) {
        project.tasks.register(SoraTask.dockerCopyFiles, Copy).configure { Copy t ->
            t.group = DOCKER_TASK_GROUP
            t.description = "Copy custom files to ${getDockerContextDir(project).path}"
            t.into(getDockerContextDir(project))

            def customFiles = dockerConfig.files
            if (customFiles == null || customFiles.isEmpty()) {
                println(format("Task ${SoraTask.dockerCopyFiles} skipped."))
                return
            }

            // copy files to context dir
            customFiles.each { source, destination ->
                def insidePath = getDockerContextRelativePath(project, destination)
                insidePath.parentFile.mkdirs() // create parent dirs if needed

                t.from(source) {
                    println(format("File mapping: $source -> $destination"))
                    // relative path to docker build context dir
                    into(new File(destination).parentFile)
                }
            }
        }
    }

    static void setupDockerfileCreateTask(Project project, @NonNull DockerConfig dockerConfig) {
        project.tasks.register(SoraTask.dockerfileCreate, Dockerfile).configure { Dockerfile t ->
            def jar = dockerConfig.jar
            def version = JavaUtils.getJavaVersion()

            t.group = DOCKER_TASK_GROUP
            t.description = "Creates dockerfile in ${getDockerContextDir(project).path}"
            t.dependsOn(SoraTask.dockerCopyJar)

            // if baseImage is defined, then use it.
            // otherwise, derive docker image from java version and use it
            t.from dockerConfig.baseImage ?: getBaseDockerImage(version)
            t.label([
                    "version"     : "${project.version}",
                    "built-date"  : "${new Date()}",
                    "built-by"    : "${System.getProperty('user.name')}",
                    "built-jdk"   : "${System.getProperty('java.version')}",
                    "built-gradle": "${project.gradle.gradleVersion}",
                    "MAINTAINER"  : "Soramitsu"
            ])
            t.instruction """ENV JAVA_OPTIONS="${getJavaOptions(version)}"
            """

            if (dockerConfig.files != null) {
                // copy files from context dir to dst
                dockerConfig.files.each { _, dst ->
                    // remove leading slash from path
                    def from = dst.replaceAll(/^\//, '')
                    t.copyFile(from, dst)
                }
            }

            // copy jar
            t.copyFile jar.name, "/${jar.name}"

            // setup tiny https://github.com/krallin/tini
            t.instruction """ENV TINI_VERSION="v0.19.0"
            """ // TODO use variable here
            t.addFile 'https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini', "/tini"
            t.runCommand "chmod +x /tini"
            t.entryPoint "/tini", "--"

            // add user
            t.runCommand "groupadd -r appuser && useradd -r -g appuser appuser"
            t.instruction "USER appuser"

            // if null, then use empty string
            def args = dockerConfig.args ?: ""

            t.defaultCommand "sh", "-c", "java \${JAVA_OPTIONS} -Djava.security.egd=file:/dev/./urandom -jar /${jar.name} $args"
        }
    }

    static void setupDockerCleanTask(Project project) {
        project.tasks.register(SoraTask.dockerClean).configure { Task t ->
            t.group = DOCKER_TASK_GROUP
            t.description = "Clean docker context dir"
            t.doLast {
                println(format('clean docker context dir'))
                getDockerContextDir(project).deleteDir()
            }
        }
    }

    static File getDockerContextDir(Project project) {
        return project.file("${project.buildDir}/docker")
    }

    static File getDockerContextRelativePath(Project project, String path) {
        return new File(getDockerContextDir(project), path)
    }

    static String getJavaOptions(int version) {
        def flags = []
        flags << "-XshowSettings:vm"
        flags << "-XX:+PrintFlagsFinal"
        switch (version) {
            case 8:
            case 9:
                flags << "-XX:+UnlockExperimentalVMOptions"
                flags << "-XX:+UseCGroupMemoryLimitForHeap"
                flags << "-XX:+UseContainerSupport"
                flags << "-XX:MaxRAMFraction=2"
                break
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                flags << "-XX:MaxRAMPercentage=70"
                flags << "-XX:MinRAMPercentage=50"
                flags << "-XX:InitialRAMPercentage=50"
                break
            default:
                throw new IllegalStateException(format("undefined/unsupported java version: ${version}"))
        }

        return flags.join(' ')
    }

    static String getBaseDockerImage(int javaVersion) {
        if (javaVersion == 11) {
            return 'openjdk:11-jre-slim'
        } else if (javaVersion == 12) {
            return 'openjdk:12-jdk-oracle'
        } else if (javaVersion == 13) {
            return 'openjdk:13'
        } else {
            // default fallback version
            return 'openjdk:8-jre-alpine'
        }
    }
}
