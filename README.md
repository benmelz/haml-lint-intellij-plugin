# haml-lint-intellij-plugin

<!-- Plugin description -->
Annotates [haml-lint][haml-lint] offenses in real time while editing haml files.
<!-- Plugin description end -->

## Installation & Usage

Installation and usage instructions are hosted on the
[Jetbrains Marketplace](https://plugins.jetbrains.com/plugin/21585-hamllint).

## Contributing

Issues and pull requests are welcome on GitHub.

### Development

This plugin is written using Kotlin on the
[Intellij Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html). Gradle manages all project
dependencies and a gradle wrapper is bundled within the repository, so the only real system requirement is a Java JDK
matching the targeted version in
[`build.gradle.kts`](https://github.com/benmelz/haml-lint-intellij-plugin/blob/main/build.gradle.kts). `ktlint` is used
to enforce code style and `semantic-release` is used to release packages/publish to the marketplace.

```bash
./gradlew check       # builds the plugin, runs linters and tests
./gradlew runIde      # builds the plugin, starts up an IDE for manual testing
```

## License

This plugin available as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

[haml-lint]: https://github.com/sds/haml-lint