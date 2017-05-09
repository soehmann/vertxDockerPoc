package de.example.soe.demo.infrastructure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import de.example.soe.demo.annotations.Verticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava.core.Vertx;

@Component
public class VerticleDeployer implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final HashMap<Class, DeploymentOptions> verticleMap = new HashMap<>();

    @PostConstruct
    void discoverVerticles() {
        Arrays.stream(applicationContext.getBeanNamesForAnnotation(Verticle.class))
                .map(applicationContext::getType)
                .forEach(beanClass -> verticleMap.put(beanClass, getDeploymentOptionsByAnnotation(beanClass.getAnnotation(Verticle.class))));
    }

    public void deployVerticles(Vertx vertx, String prefix) {
        verticleMap.forEach((verticleClass, options) -> vertx.deployVerticle(prefix + ":" + verticleClass.getName(), options));
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private DeploymentOptions getDeploymentOptionsByAnnotation(Verticle verticle) {
        return new DeploymentOptions()
                .setInstances(verticle.instances())
                .setIsolationGroup(Objects.equals(verticle.isolationGroup(), "") ? null : verticle.isolationGroup())
                .setWorkerPoolName(Objects.equals(verticle.workerPoolName(), "") ? null : verticle.workerPoolName())
                .setMultiThreaded(verticle.multiThreaded())
                .setHa(verticle.ha())
                .setWorker(verticle.worker())
                .setWorkerPoolSize(verticle.workerPoolSize())
                .setMaxWorkerExecuteTime(verticle.maxWorkerExecuteTime());
    }
}
