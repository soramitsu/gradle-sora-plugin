package jp.co.soramitsu.devops

import groovy.transform.CompileStatic

@CompileStatic
class SoraTask {

    /// java
    public static final String build = "build"
    public static final String clean = "clean"
    public static final String test = "test"
    public static final String check = "check"

    /// docker
    public static final String dockerfileCreate = "dockerfileCreate"
    public static final String dockerPush = "dockerPush"
    public static final String dockerCopyJar = "dockerCopyJar"
    public static final String dockerCopyFiles = "dockerCopyFiles"
    public static final String dockerBuild = "dockerBuild"
    public static final String dockerClean = "dockerClean"
    public static final String dockerVersion = "dockerVersion"

    /// misc
    public static final String printOsInfo = "printOsInfo"
    public static final String printVersion = "printVersion"
    public static final String printConfig = "printConfig"
    public static final String printDockerImage = "printDockerImage"

    /// coverage
    public static final String coverage = "coverage"
}
