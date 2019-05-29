package jp.co.soramitsu.devops.docker

import groovy.transform.CompileStatic

@CompileStatic
class Sanitize {

    static String tag(String t) {
        t
                ?.replaceAll("http://", "")
                ?.replaceAll("https://", "")
                ?.replaceAll("//", "/")
    }
}
