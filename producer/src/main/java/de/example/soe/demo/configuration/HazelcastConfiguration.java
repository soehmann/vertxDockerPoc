package de.example.soe.demo.configuration;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SemaphoreConfig;

@Configuration
public class HazelcastConfiguration {

    private final List<String> clusterMembers;

    private final String groupName;
    private final String groupPasswort;

    public HazelcastConfiguration(
            @Value("#{'${hazelcast.cluster.members}'.split(',')}") List<String> clusterMembers,
            @Value("${hazelcast.group.name}") String groupName,
            @Value("${hazelcast.group.password}") String groupPasswort) {
        this.groupName = groupName.trim();
        this.groupPasswort = groupPasswort.trim();
        this.clusterMembers = clusterMembers.stream()
                .map(String::trim)
                .filter(member -> !member.isEmpty())
                .collect(Collectors.toList());

        assert !groupName.isEmpty() : "take care that 'hazelcast.group.name' is not empty";
        assert !groupPasswort.isEmpty() : "take care that 'hazelcast.group.password' is not empty";
    }


    @Bean
    public Config hazelcastConfig() {

        Config config = new Config()
                .setProperty("hazelcast.memcache.enabled", Boolean.FALSE.toString())
                .setProperty("hazelcast.rest.enabled", Boolean.FALSE.toString())
                .setProperty("hazelcast.wait.seconds.before.join", "0")
                .setProperty("hazelcast.shutdown.enabled", Boolean.FALSE.toString())
                .setProperty("hazelcast.logging.type", "slf4j");

        config.getGroupConfig()
                .setName(groupName)
                .setPassword(groupPasswort);

        config.getManagementCenterConfig()
                .setEnabled(false);

        config.getExecutorConfig("default")
                .setPoolSize(10)
                .setQueueCapacity(50);

        config.getPartitionGroupConfig().setEnabled(false);

        NetworkConfig networkConfig = config.getNetworkConfig();

        JoinConfig joinConfig = networkConfig.getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getAwsConfig().setEnabled(false);
        joinConfig.getTcpIpConfig()
                .setEnabled(true)
                .setMembers(clusterMembers);

        config.addMultiMapConfig(new MultiMapConfig()
                .setName("__vertx.subs")
                .setBackupCount(1));

        config.addMapConfig(new MapConfig()
                .setName("__vertx.haInfo")
                .setBackupCount(1)
                .setMaxIdleSeconds(0)
                .setMaxSizeConfig(new MaxSizeConfig().setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy.PER_NODE).setSize(0))
                .setEvictionPercentage(25)
                .setMergePolicy("com.hazelcast.map.merge.LatestUpdateMapMergePolicy"));

        config.addSemaphoreConfig(new SemaphoreConfig()
                .setName("__vertx.*")
                .setInitialPermits(1));

        return config;
    }

}
