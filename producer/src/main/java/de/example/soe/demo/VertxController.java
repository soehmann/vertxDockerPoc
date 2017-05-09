package de.example.soe.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.example.soe.demo.domain.KatalogIdentifier;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rx.Single;

@RestController
@Slf4j
@RequiredArgsConstructor
public class VertxController {

    private static final String SOE_EXAMPLE_KATALOG_EVENT = "soe.example.katalog";

    private final Vertx vertx;

    @GetMapping(value = "/katalogevent/{siteId}")
    public ResponseEntity<String> testController(@PathVariable("siteId") long siteId) {

        log.info("Start to trigger event with siteId: {}", siteId);

        Single<Message<String>> messageSingle = vertx.eventBus()
                .rxSend(SOE_EXAMPLE_KATALOG_EVENT, new KatalogIdentifier(siteId), new DeliveryOptions().setSendTimeout(5000L));

        String responseString = messageSingle
                .map(Message::body)
                .onErrorReturn(t -> {
                    log.error("Failed to send katalog event for siteId: {} and pathId: {} by error: {}", siteId, t.getMessage());
                    log.error("Vertx send error.", t);
                    return null;
                })
                .toBlocking()
                .value();

        if (responseString == null) {
            log.error("No response for siteId: {}", siteId);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            log.info("Received triggered event with siteId: {}", siteId);
            return ResponseEntity.ok(responseString);
        }
    }
}
