package io.quarkiverse.discord4j.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "quarkus.discord4j")
public interface Discord4jBuildTimeConfig {
    /**
     * Healthcheck configuration
     */
    HealthConfig health();

    interface HealthConfig {
        /**
         * Whether a health check should be published for the Gateway clients managed by this extension if the
         * quarkus-smallrye-health extension is present
         */
        @WithDefault("true")
        boolean enabled();
    }

    /**
     * Metrics configuration
     */
    MetricsConfig metrics();

    interface MetricsConfig {
        /**
         * Whether metrics should be collected for the Gateway clients managed by this extension if the
         * quarkus-micrometer or quarkus-smallrye-metrics extension is present
         */
        @WithDefault("false")
        boolean enabled();
    }
}
