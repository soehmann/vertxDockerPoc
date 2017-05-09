package de.idealo.seo.demo;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@Slf4j
public class ConnectionIntegrationTest {

    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        restTemplate = new TestRestTemplate();
    }

    @Test
    public void sucessfulConnection_ProducerToProducer() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        for (int i = 0; i < 10; i++) {
            ResponseEntity<String> response = this.restTemplate.exchange("http://producerA:8080/katalogevent/1", HttpMethod.GET, entity, String.class);

            System.out.println("Response no: " + i + " status: " + response.getStatusCode() + " body: " + response.getBody());
            //assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            //assertThat(response.getBody()).isEqualTo("{}");
        }
    }
}
