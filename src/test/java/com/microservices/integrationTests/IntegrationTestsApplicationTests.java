package com.microservices.integrationTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//@ActiveProfiles("test")
public class IntegrationTestsApplicationTests {

	private RestTemplate restTemplate = new RestTemplate();
	private Integer serverPort = 8080;

	private String getUrl() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void IsOrderFormDisplayed() {
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				getUrl() + "/order/form.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("Order : Add"));
	}

	@Test
	public void contextLoads() {
	}

}
