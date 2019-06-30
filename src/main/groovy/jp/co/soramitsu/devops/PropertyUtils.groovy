package jp.co.soramitsu.devops

class PropertyUtils {

    static final Optional<Object> getEnv(String name) {
        return Optional.ofNullable(System.getenv(name))
    }
}
