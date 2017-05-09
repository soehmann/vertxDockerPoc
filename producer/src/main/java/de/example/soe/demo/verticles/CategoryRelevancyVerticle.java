package de.example.soe.demo.verticles;

import de.example.soe.demo.domain.KatalogIdentifier;
import de.example.soe.demo.annotations.MessageListener;
import de.example.soe.demo.annotations.Verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Verticle(instances = 3)
public class CategoryRelevancyVerticle extends AbstractVerticle {

    private final MongoClient mongoClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus()
                .consumer("soe.example.katalog", this::messageHandler)
                .completionHandler(startFuture.completer());
    }

    @MessageListener("soe.example.katalog")
    public void messageHandler(Message<KatalogIdentifier> message) {
        log.info("Handle message for siteId: {}", message.body().getSiteId());
        JsonObject mongoQuery = new JsonObject().put("_id", JsonObject.mapFrom(message.body()));

        mongoClient.rxFindOne("katalog", mongoQuery, null)
                .map(JsonObject::encodePrettily)
                .onErrorReturn(t -> "{}")
                .subscribe(message::reply);
    }

}
