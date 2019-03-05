package jp.co.soramitsu.devops.docker

class Sanitize {

    static String tag(String t) {
        t
                ?.replaceAll("http://", "")
                ?.replaceAll("https://", "")
                ?.replaceAll("//", "/")
    }
}
