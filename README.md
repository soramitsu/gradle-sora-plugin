[![Build Status](https://travis-ci.org/soramitsu/gradle-sora-plugin.svg?branch=master)](https://travis-ci.org/soramitsu/gradle-sora-plugin)

# gradle-devops-helper

Min supported gradle 4.8
Works with gradle 7

## Usage

https://plugins.gradle.org/plugin/jp.co.soramitsu.sora-plugin

## Tasks

| **Task name**    | **Tasks executed**                                 | **Description**                                            |
|------------------|----------------------------------------------------|------------------------------------------------------------|
| build            | compile + build                                    | Builds, does NOT execute check or test.                    |
| check            | build + test + check                               | Builds, then executes static code analyzers                |
| test             | build + test                                       | Builds, then executes all registered tests                 |
| coverage         | build + test + coverage                            | Builds, executes tests and then collects code coverage.    |
| printOsInfo      | printOsInfo                                        | Prints information about OS and project                    |
| printVersion     | printVersion                                       | Prints project version based on git                        |
| printConfig      | printConfig                                        | Prints plugin configuration                                |
| printDockerImage | printDockerImage                                   | Prints Docker Image that will be used in dockerPush        |
| dockerVersion    | dockerVersion                                      | Prints current docker version                              |
| dockerClean      | dockerClean                                        | Cleans docker build context (build/docker)                 |
| dockerfileCreate | dockerClean + dockerfileCreate                     | Generates Dockerfile in build context                      |
| dockerCopyJar    | build + dockerCopyJar                              | Builds and copies JAR to build context                     |
| dockerCopyFiles  | dockerCopyFiles                                    | Copies custom files specified to build context             |
| dockerBuild      | dockerfileCreate + dockerCopyJar + dockerCopyFiles | Builds image with autogenerated tag                        |
| dockerPush       | dockerBuild + dockerPush                           | Pushes image to docker registry                            |

**NOTE**:

- to enable `docker*` tasks, please apply plugin `application`
- `build`, `test`, `check` tasks will be modified ONLY if `java` plugin is applied

## Configuration

All fields are optional.

build.gradle:

```gradle
soramitsu {
  projectGroup = 'sora' # define namespace for your project. default: null
  docker {
    # base docker image to be used (Dockerfile FROM instruction)
    # default: detected based on java version. fallback: openjdk:8-jre-alpine
    baseImage = 'openjdk:8u191-jre-alpine'
    # path to output JAR which is going to be dockerized
    jar = new File("build/libs/${project.name}-${project.version}.jar") 
    # manually specified docker tag. if null, automatic versioning based on git is used 
    # it replaces only 'version' part, e.g. ${name}:${tag}. Example: soramitsu/example:${tag}
    tag = "custom-tag"  
    # to enable dockerPush task, define registry credentials
    registry {
      url = 'https://<host>:<port>' 
      username = '<username>'
      password = '<password>'
      email = '<email>'  # optional
    }
    # files that will be added to the docker image (optional)
    # host fs: docker image fs
    files = [local-file.txt: /file.txt]
    # optional startup arguments for java
    # java -jar ... $args
    runArgs = "-version"
    # optional build arguments for docker
    buildArgs = ['--platform':'linux/amd64']
  }
}
```

Docker registry data can be defined from env vars. Env vars ALWAYS override any value from build.gradle:

```bash
DOCKER_REGISTRY_URL="https://<host>:<port>"
DOCKER_REGISTRY_USERNAME="<username>"
DOCKER_REGISTRY_PASSWORD="<password>"
DOCKER_REGISTRY_EMAIL="<email>"
```

## Features

- [x] automatic versioning based on git ([uses gitVersion](https://github.com/palantir/gradle-git-version/))
- [x] build does not invoke tests (only with plugin `java`)
- [ ] separate `check` task for static code analysis; formatting analysis
- [x] `coverage` task, which invokes all tests and calculates coverage.
- [x] `dockerBuild` task to build docker image including custom arguments
- [x] automatic tag generation. Tag consists of `${url}/${projectGroup}/${project.name}:${project.version}`
    - url - url of docker registry without http(s)://
    - projectGroup - e.g. sora, bakong, etc.
    - project.name - defined in settings.gradle
    - project.version - git version
- [x] configuration of docker registry from env vars

## Examples

See [projects](./projects) directory.
