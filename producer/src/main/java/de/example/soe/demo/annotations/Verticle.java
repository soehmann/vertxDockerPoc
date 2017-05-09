package de.example.soe.demo.annotations;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_PROTOTYPE)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Verticle {

    int instances() default 1;

    //These Strings Must apparently be null in DeplyomentOptions...
    String isolationGroup() default "";
    String workerPoolName() default "";

    boolean multiThreaded() default false;
    boolean ha() default false;

    boolean worker() default false;
    int workerPoolSize() default 20;
    long maxWorkerExecuteTime() default 60000000000L;

}
