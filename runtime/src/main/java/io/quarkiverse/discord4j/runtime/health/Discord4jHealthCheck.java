package io.quarkiverse.discord4j.runtime.health;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import discord4j.core.GatewayDiscordClient;
import discord4j.gateway.GatewayClient;
import discord4j.gateway.GatewayClientGroup;
import io.quarkiverse.discord4j.runtime.Discord4jStarter;
import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;

@Readiness
public class Discord4jHealthCheck implements AsyncHealthCheck {
    public static final String NAME = "Discord4J health check";

    @Inject
    Discord4jStarter starter;

    @Inject
    Instance<GatewayDiscordClient> gatewayInstance;

    @Override
    public Uni<HealthCheckResponse> call() {
        if (!starter.isGatewayInitialized()) {
            return Uni.createFrom().item(HealthCheckResponse.named(NAME).down()
                    .withData("reason", "Gateway not initialized - no Discord bot token configured").build());
        }

        if (starter.isMockMode()) {
            return Uni.createFrom().item(HealthCheckResponse.named(NAME).up()
                    .withData("mode", "mock").build());
        }

        GatewayDiscordClient gateway = gatewayInstance.get();
        HealthCheckResponseBuilder builder = HealthCheckResponse.named(NAME).up();

        GatewayClientGroup gatewayClientGroup = gateway.getGatewayClientGroup();
        List<Uni<?>> unis = new ArrayList<>();
        for (int i = 0; i < gatewayClientGroup.getShardCount(); i++) {
            Optional<GatewayClient> client = gatewayClientGroup.find(i);
            if (client.isEmpty()) {
                continue;
            }

            String responseTime = client.get().getResponseTime().toString().substring(2).toLowerCase();
            String shardName = "shard." + i;

            unis.add(UniReactorConverters.<Boolean> fromMono().from(client.get().isConnected().doOnNext(connected -> {
                if (connected) {
                    builder.withData(shardName + ".response.time", responseTime);
                } else {
                    builder.down().withData("reason", shardName + " is not connected");
                }
            })));
        }

        return Uni.combine().all().unis(unis).with(ignored -> builder.build());
    }
}
