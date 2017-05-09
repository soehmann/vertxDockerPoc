package de.example.soe.demo.monitoring;

import java.io.Serializable;
import java.util.Set;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import io.vertx.rxjava.core.Vertx;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Component
@RequiredArgsConstructor
public class VertxHealthIndicator extends AbstractHealthIndicator {

    private final Vertx vertx;

    @Override
    protected void doHealthCheck(final Health.Builder builder) throws Exception {

        final Set<String> deploymentIds = vertx.deploymentIDs();
        final Boolean isMetricsEnabled = vertx.isMetricsEnabled();
        final boolean isClustered = vertx.isClustered();

        (deploymentIds.isEmpty() ? builder.down() : builder.up())
                .withDetail("healthInfo", new HealthInfo(deploymentIds, isClustered, isMetricsEnabled));
    }

    @Value
    private static class HealthInfo implements Serializable {
        private final Set<String> verticleIds;
        private final boolean isClustered;
        private final boolean isMetricEnabled;
    }
}
