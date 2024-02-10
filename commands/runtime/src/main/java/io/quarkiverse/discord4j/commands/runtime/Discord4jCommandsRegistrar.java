package io.quarkiverse.discord4j.commands.runtime;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;
import org.reactivestreams.Publisher;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;
import io.quarkiverse.discord4j.commands.runtime.config.Discord4jCommandsConfig;
import io.quarkiverse.discord4j.commands.runtime.config.GuildCommandsConfig;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.unchecked.Unchecked;
import reactor.core.publisher.Mono;

@ApplicationScoped
public class Discord4jCommandsRegistrar {
    private static final Logger LOGGER = Logger.getLogger(Discord4jCommandsRegistrar.class);
    public static volatile List<String> globalCommands = new ArrayList<>();
    public static volatile Map<String, List<String>> guildCommands = new HashMap<>();

    @Inject
    ObjectMapper objectMapper;

    private static List<ApplicationCommandRequest> convert(List<String> commands, ObjectMapper objectMapper) {
        return commands.stream()
                .map(Unchecked.function(c -> objectMapper.readValue(c, ApplicationCommandRequest.class)))
                .collect(Collectors.toList());
    }

    private static Predicate<ApplicationCommandData> filter(List<ApplicationCommandRequest> commands) {
        return c -> {
            for (ApplicationCommandRequest command : commands) {
                if (c.name().equals(command.name()) && c.type().equals(command.type())) {
                    return true;
                }
            }
            return false;
        };
    }

    public void register(@Observes StartupEvent startup, GatewayDiscordClient gateway, Discord4jCommandsConfig config) {
        List<Publisher<?>> publishers = new ArrayList<>();
        Mono<Long> idMono = gateway.rest().getApplicationId();
        ApplicationService app = gateway.rest().getApplicationService();

        List<ApplicationCommandRequest> globalCommandRequests = convert(globalCommands, objectMapper);
        String globalCommandsPath = config.globalCommands.path;

        if (!globalCommandRequests.isEmpty()) {
            publishers.add(idMono.flatMapMany(id -> app.bulkOverwriteGlobalApplicationCommand(id, globalCommandRequests)));

            if (config.globalCommands.deleteMissing) {
                publishers.add(idMono.flatMapMany(id -> app.getGlobalApplicationCommands(id)
                        .filter(filter(globalCommandRequests))
                        .delayUntil(command -> app.deleteGlobalApplicationCommand(id, command.id().asLong())))
                        .doOnNext(command -> LOGGER.debugf(
                                "Deleted global command %s as it does not have a matching JSON file in %s",
                                command.name(), globalCommandsPath)));
            }
        }

        for (Map.Entry<String, List<String>> entry : guildCommands.entrySet()) {
            GuildCommandsConfig commandsConfig = config.guildCommands.get(entry.getKey());
            String guildCommandsPath = commandsConfig.path.orElse(globalCommandsPath + '/' + entry.getKey());

            List<ApplicationCommandRequest> guildCommandRequests = convert(entry.getValue(), objectMapper);

            publishers.add(
                    idMono.flatMapMany(
                            id -> app.bulkOverwriteGuildApplicationCommand(id, commandsConfig.guildId, guildCommandRequests)));

            if (commandsConfig.deleteMissing) {
                publishers.add(idMono.flatMapMany(id -> app.getGuildApplicationCommands(id, commandsConfig.guildId)
                        .filter(filter(guildCommandRequests))
                        .delayUntil(command -> app.deleteGuildApplicationCommand(id, commandsConfig.guildId,
                                command.id().asLong())))
                        .doOnNext(command -> LOGGER.debugf(
                                "Deleted guild command %s from guild %s (%s) as it does not have a matching JSON file in %s",
                                command.name(), entry.getKey(), commandsConfig.guildId, guildCommandsPath)));
            }
        }

        Mono.when(publishers).block();
    }
}
