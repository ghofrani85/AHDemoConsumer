package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class homePageControllerTest {
	
	@Autowired
    private TestRestTemplate restTemplate;
	
	@LocalServerPort
	private int port = 8081;

    @Test
    public void shouldReturnIndexPage() throws Exception {
        
        String index = "src/main/resources/static/index.html";
        String html = Files.readString(Paths.get(index));
        String responseBody = restTemplate.getForObject("/", String.class);
        assertThat(responseBody).isEqualTo(html);
    }
}
