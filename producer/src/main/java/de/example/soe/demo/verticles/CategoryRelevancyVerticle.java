package de.example.soe.demo.verticles;

import de.example.soe.demo.annotations.MessageListener;
import de.example.soe.demo.annotations.Verticle;
import de.example.soe.demo.domain.KatalogIdentifier;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Verticle(instances = 3)
public class CategoryRelevancyVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus()
                .consumer("soe.example.katalog", this::messageHandler)
                .completionHandler(startFuture.completer());
    }

    @MessageListener("soe.example.katalog")
    public void messageHandler(Message<KatalogIdentifier> message) {
        log.info("Handle message into verticle for siteId: {}", message.body().getSiteId());
        message.reply("{\"katalog\":" + message.body().getSiteId() + ",\"relevancy\":true}");
    }

}
