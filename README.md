# Quarkus Discord4J
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-5-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.discord4j/quarkus-discord4j?logo=apache-maven&style=for-the-badge)](https://central.sonatype.com/artifact/io.quarkiverse.discord4j/quarkus-discord4j)

Quarkus Discord4J is an experimental [Quarkus](https://quarkus.io) extension which allows you to develop a WebSocket-based (Gateway) Discord bot using the [Discord4J](https://github.com/Discord4J/Discord4J) library with minimal boilerplate.

Your bot code will look like this:
```java
class MyDiscordBot {
    Mono<Void> onMessageCreate(@GatewayEvent MessageCreateEvent event) {
        return event.getMessage().addReaction(ReactionEmoji.of("🤖"));
    }
}
```

The code above listens to the `MESSAGE_CREATE` Gateway event and adds a reaction of 🤖 to every received message.

With the additional [commands extension](https://quarkiverse.github.io/quarkiverse-docs/quarkus-discord4j/dev/commands.html), you can also automatically register and listen for [application commands](https://discord.com/developers/docs/interactions/application-commands).

Because it's a Quarkus extension, all the standard Quarkus goodies apply:
* Quarkus live reload to detect user code changes and restart your bot automatically (and delay incoming events until after your bot restarts to increase developer joy!)
* Native executable generation via GraalVM or Mandrel
* Optional automatic metrics collection
* And more!

Inspired by the [Quarkus GitHub App extension](https://github.com/quarkiverse/quarkus-github-app).

## Documentation
Please refer to the [extension documentation](https://quarkiverse.github.io/quarkiverse-docs/quarkus-discord4j/dev/index.html) to learn how to get started and more.

## Examples
> Does your Discord bot use this extension? Please open a PR and add it here!
## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/cameronprater"><img src="https://avatars.githubusercontent.com/u/40479627?v=4?s=100" width="100px;" alt="cameronprater"/><br /><sub><b>cameronprater</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-discord4j/commits?author=cameronprater" title="Code">💻</a> <a href="#maintenance-cameronprater" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://lesincroyableslivres.fr/"><img src="https://avatars.githubusercontent.com/u/1279749?v=4?s=100" width="100px;" alt="Guillaume Smet"/><br /><sub><b>Guillaume Smet</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-discord4j/commits?author=gsmet" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://julien.ponge.org/"><img src="https://avatars.githubusercontent.com/u/25961?v=4?s=100" width="100px;" alt="Julien Ponge"/><br /><sub><b>Julien Ponge</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-discord4j/commits?author=jponge" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://blog.omatheusmesmo.dev"><img src="https://avatars.githubusercontent.com/u/99829531?v=4?s=100" width="100px;" alt="Matheus Oliveira"/><br /><sub><b>Matheus Oliveira</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-discord4j/commits?author=omatheusmesmo" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/artkonr"><img src="https://avatars.githubusercontent.com/u/40516045?v=4?s=100" width="100px;" alt="Artem Kononov"/><br /><sub><b>Artem Kononov</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-discord4j/commits?author=artkonr" title="Documentation">📖</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
