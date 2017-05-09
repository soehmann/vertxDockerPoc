package de.example.soe.demo.infrastructure;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
public class VerticleInitializer {


    @Before("execution(* start(..))")
    public void TestPointcut(JoinPoint joinPoint) {
        log.warn("test");
    }
}
