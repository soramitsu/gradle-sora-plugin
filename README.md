# gradle-devops-helper

Min supported gradle 4.8

## Tasks

| Task name    | Env/Prop | Tasks executed          | Description                                             |
|--------------|----------|-------------------------|---------------------------------------------------------|
| build        | -        | compile + build         | Builds, does NOT execute check or test.                 |
| check        | -        | build + test + check    | Builds, then executes static code analyzers             |
| test         | -        | build + test            | Builds, then executes all registered tests              |
| coverage     | -        | build + test + coverage | Builds, executes tests and then collects code coverage. |
| osInfo       | -        | osInfo                  | Prints information about OS and project                 |
| printVersion | -        | printVersion            | Prints project version based on git                     |
