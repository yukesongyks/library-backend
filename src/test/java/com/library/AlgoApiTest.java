package com.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlgoApiTest {
    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void helloworld() {
        ResponseEntity<String> resp = restTemplate.getForEntity(url("/api/algo/helloworld"), String.class);
        assertTrue(resp.getStatusCode().is2xxSuccessful());
        assertTrue(resp.getBody().contains("Hello"));
    }

    @Test
    void hash() {
        ResponseEntity<String> resp = restTemplate.getForEntity(url("/api/algo/hash?input=test"), String.class);
        assertTrue(resp.getStatusCode().is2xxSuccessful());
        assertTrue(resp.getBody().contains("hash"));
    }

    @Test
    void bubblesort() {
        ResponseEntity<String> resp = restTemplate.getForEntity(url("/api/algo/bubblesort?input=5,3,8,1"), String.class);
        assertTrue(resp.getStatusCode().is2xxSuccessful());
        assertTrue(resp.getBody().contains("bubblesort"));
    }

    @Test
    void stats() {
        restTemplate.getForEntity(url("/api/algo/helloworld"), String.class);
        ResponseEntity<String> resp = restTemplate.getForEntity(url("/api/stats"), String.class);
        assertTrue(resp.getStatusCode().is2xxSuccessful());
        assertTrue(resp.getBody().contains("userType"));
    }
}
