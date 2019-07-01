package jp.co.soramitsu.devops.utils

class JavaUtils {
    static int getJavaVersion(){
        def version = System.getProperty('java.version').split('\\.')
        if(version[0] == '1') {
            return version[1].toInteger()
        } else {
            return version[0].toInteger()
        }
    }
}
