# gradle-devops-helper

Min supported gradle 4.8

## Tasks

| Task name        | Env/Prop | Example                 | Tasks executed                  | Description                                 |
|------------------|----------|-------------------------|---------------------------------|---------------------------------------------|
| build            | -        | gradle build            | compile + build                 | Builds, does NOT execute check or test.     |
| check            | -        | gradle check            | build + check                   | Builds, then executes static code analyzers |
| test             | -        | gradle test             | build + test                    | Builds, then executes all registered tests  |
| jacocoTestReport | -        | gradle jacocoTestReport | build + test + jacocoTestReport | Builds, then collects code coverage.        |
