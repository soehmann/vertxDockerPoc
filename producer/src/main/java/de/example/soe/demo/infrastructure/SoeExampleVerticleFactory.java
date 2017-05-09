package de.example.soe.demo.infrastructure;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SoeExampleVerticleFactory implements VerticleFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public boolean blockingCreate() {
        return true;
    }

    @Override
    public String prefix() {
        return "soe.example";
    }

    @Override
    public Verticle createVerticle(final String verticleIdentifier, final ClassLoader classLoader) throws Exception {
        String clazz = VerticleFactory.removePrefix(verticleIdentifier);
        return (Verticle) applicationContext.getBean(Class.forName(clazz));
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
