# Quarkus Discord4J

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.discord4j/quarkus-discord4j?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.discord4j/quarkus-discord4j)

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