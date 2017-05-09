package de.example.soe.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoClient;

@Configuration
public class MongoClientConfiguration {

    private final Vertx vertx;
    private final String connectionString;

    public MongoClientConfiguration(final Vertx vertx, @Value("${spring.data.mongodb.uri}") String connectionString) {
        this.vertx = vertx;
        this.connectionString = connectionString;
    }

    @Bean
    public MongoClient mongoClient() {
        JsonObject config = new JsonObject().put("connection_string", connectionString);
        return MongoClient.createShared(vertx, config, "mongoPool");
    }
}
