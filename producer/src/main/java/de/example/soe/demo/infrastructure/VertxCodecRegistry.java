package de.example.soe.demo.infrastructure;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import io.vertx.rxjava.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VertxCodecRegistry implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final List<AbstractCodec<?>> codecList = new ArrayList();

    @PostConstruct
    void discoverEncoder() {
        applicationContext.getBeansOfType(AbstractCodec.class)
                .forEach((s,codec) -> codecList.add(codec));
    }

    public void registerCodecs(Vertx vertx) {
        codecList.forEach(codec -> this.registerCodec(vertx, codec));
    }

    private void registerCodec(Vertx vertx, AbstractCodec<?> codec) {
        vertx.eventBus().getDelegate().registerDefaultCodec(codec.getEncodedClass(), codec);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
