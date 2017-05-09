package de.example.soe.demo.monitoring;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import com.hazelcast.core.Client;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiMap;

import io.vertx.spi.cluster.hazelcast.impl.HazelcastClusterNodeInfo;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HazelcastHealthIndicator extends AbstractHealthIndicator {

    private final HazelcastInstance hazelcastInstance;

    @Override
    protected void doHealthCheck(final Health.Builder builder) {

        Set<Member> clusterMembers = hazelcastInstance.getCluster().getMembers();

        if (clusterMembers.isEmpty()) {
            builder.down();
        } else {
            final String members = clusterMembers
                    .stream()
                    .map(m -> m.getSocketAddress().getHostString())
                    .collect(Collectors.joining(", "));

            builder.up()
                    .withDetail("clusterMembers", members)
                    .withDetail("__vertx.subs", getNodeInfoList())
                    .withDetail("connectedClients", connectClients());
        }
    }

    @SuppressWarnings("squid:S1166")
    private List<NodeInfo> getNodeInfoList() {
        try {
            MultiMap<String, HazelcastClusterNodeInfo> vertxSubs = hazelcastInstance.getMultiMap("__vertx.subs");
            return vertxSubs
                    .entrySet()
                    .stream()
                    .map(e -> new NodeInfo(e.getKey(), e.getValue().nodeId, e.getValue().serverID.host, e.getValue().serverID.port))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("could not read __vertx.subs by error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<ConnectedClient> connectClients() {
        final Collection<Client> clients = hazelcastInstance.getClientService().getConnectedClients();

        return clients.stream().map(client -> {
            return new ConnectedClient(
                    client.getClientType().name(),
                    client.getUuid(),
                    client.getSocketAddress().getHostName(),
                    client.getSocketAddress().getPort());
        }).collect(Collectors.toList());
    }

    @Value
    private static class ConnectedClient implements Serializable {
        private static final long serialVersionUID = 3149123789456247185L;

        String type;
        String uuid;
        String host;
        int port;
    }

    @Value
    private static class NodeInfo implements Serializable {
        private static final long serialVersionUID = 3349188193356247785L;
        String vertxSubscriptionKey;
        String nodeId;
        String host;
        int port;
    }
}
