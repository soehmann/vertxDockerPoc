package de.example.soe.demo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@EnableAutoConfiguration
public class ProducerApp {

    public static void main(final String... args) {
        new SpringApplicationBuilder(ProducerApp.class).run(args);
    }

}
