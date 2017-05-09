package de.example.soe.demo.configuration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;

import de.example.soe.demo.infrastructure.VertxCodecRegistry;
import de.example.soe.demo.infrastructure.VerticleDeployer;

import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class VertxConfiguration {

    private final HazelcastInstance hazelcastInstance;

    private final VerticleFactory verticleFactory;
    private final VerticleDeployer verticleDeployer;
    private final VertxCodecRegistry vertxCodecRegistry;

    private final String hostName;
    private final int publicPort;
    private Vertx vertx;

    public VertxConfiguration(final HazelcastInstance hazelcastInstance,
            final VerticleFactory verticleFactory,
            final VerticleDeployer verticleDeployer,
            final VertxCodecRegistry vertxCodecRegistry,
            @Value("${vertx.host.name:localhost}") String hostName,
            @Value("${vertx.host.public-port:40001}") int publicPort) {
        this.hazelcastInstance = hazelcastInstance;
        this.verticleFactory = verticleFactory;
        this.verticleDeployer = verticleDeployer;
        this.vertxCodecRegistry = vertxCodecRegistry;
        this.hostName = hostName;
        this.publicPort = publicPort;
    }

    @PostConstruct
    void createVertx() throws ExecutionException, InterruptedException, SocketException {
        VertxOptions options = new VertxOptions()
                .setClusterManager(new HazelcastClusterManager(hazelcastInstance))
                .setClusterHost(hostName);

        listNetworkinterfaces();

        ObservableFuture<Vertx> vertxObservable = RxHelper.observableFuture();

        Vertx.clusteredVertx(options,vertxObservable.toHandler());

        this.vertx = vertxObservable
                .doOnError(this::handleVertxInstantiationError)
                .map(this::initVertx)
                .toBlocking()
                .first();
    }

    private Vertx initVertx(Vertx vertx) {
        vertxCodecRegistry.registerCodecs(vertx);
        vertx.getDelegate().registerVerticleFactory(verticleFactory);
        verticleDeployer.deployVerticles(vertx, verticleFactory.prefix());
        return vertx;
    }

    private void handleVertxInstantiationError(Throwable t) {
        log.error("Error instantiating vert.x: "+t.getMessage());
        throw new RuntimeException(t);
    }

    @Bean(destroyMethod = "")
    Vertx vertx() {
        return vertx;
    }

    @PreDestroy
    void close() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.close(ar -> future.complete(null));
        future.get();
    }

    private void listNetworkinterfaces() throws SocketException {
        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        Collections.list(networkInterfaces).forEach(networkInterface -> {
            System.out.println("Name: " + networkInterface.getName() + ", Displayname: " + networkInterface.getDisplayName());
            System.out.println("InterfaceAddresses: ");
            networkInterface.getInterfaceAddresses().forEach(interfaceAddress -> {
                final InetAddress inetAddress = interfaceAddress.getAddress();
                System.out.println("HostAddress: " + inetAddress.getHostAddress());
                System.out.println("CanonicalHostName: " + inetAddress.getCanonicalHostName());
                System.out.println("HostName: " + inetAddress.getHostName());
                System.out.println("IsAnyLocal: " + inetAddress.isAnyLocalAddress());
                System.out.println("IsLinkLocal: " + inetAddress.isLinkLocalAddress());
                System.out.println("IsLoopback: " + inetAddress.isLoopbackAddress());
                System.out.println("IsMulticast: " + inetAddress.isMulticastAddress());
                System.out.println("IsSiteLocal: " + inetAddress.isSiteLocalAddress());
                try {
                    System.out.println("IsReachable: " + inetAddress.isReachable(100));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("############################################");
        });
    }


}
