package com.microservices.integrationTests;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class MicroserviceConsulIntegrationTests {

	private RestTemplate restTemplate = new RestTemplate();
	private Integer serverPort = 8080;

	private String getUrl() {
		return "http://localhost:" + serverPort;
	}

	@BeforeEach
	public void loadHomePage(){
		// Home Page Load
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				getUrl(), String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Order Processing</title>"));
	}

	@Test
	public void addCustomerInfo() {
		// Open Customer Page
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				getUrl() + "/customer/list.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Customer : View all</title>"));

		// Navigate to Add Customer Page
		resultEntity = restTemplate.getForEntity(
				getUrl() + "/customer/form.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Customer : Edit</title>"));

		// Submit Add Customer Request
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("firstname", "Juergen1");
		map.add("name", "Hoeller1");
		map.add("street", "Schlossallee");
		map.add("city", "Linz");
		map.add("email", "springjuergen1@twitter.com");
		resultEntity = restTemplate.postForEntity(getUrl() + "/customer/form.html", map,
				String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Success</title>"));

		//Check if Customer is added
		resultEntity = restTemplate.getForEntity(
				getUrl() + "/customer/list.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Customer : View all</title>"));
		assertTrue(resultEntity.getBody().contains("<td>Hoeller1</td>"));
		assertTrue(resultEntity.getBody().contains("<td>Juergen1</td>"));
	}

	@Test
	public void addNewItem() {
		// Open Catalog Page
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				getUrl() + "/catalog/list.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Item : View all</title>"));

		// Navigate to Add Item Page
		resultEntity = restTemplate.getForEntity(
				getUrl() + "/catalog/form.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Item : Edit</title>"));

		// Submit Add Item Request
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("name", "Iphone Ultra Pro Pro Max");
		map.add("price", "10000");
		resultEntity = restTemplate.postForEntity(getUrl() + "catalog/form.html", map,
				String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Success</title>"));

		//Check if Item is added
		resultEntity = restTemplate.getForEntity(
				getUrl() + "/catalog/list.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("Iphone Ultra Pro Pro Max"));
		assertTrue(resultEntity.getBody().contains("<td>10000.0</td>"));
	}

	@Test
	public void searchItem() {
		// Open Search Catalog Page
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				getUrl() + "/catalog/searchForm.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Item : Search</title>"));

		// Search for an item
		resultEntity = restTemplate.getForEntity(
				getUrl() + "catalog/searchByName.html?query=iPod&submit=", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<td>iPod</td>"));
	}

	@Test
	public void searchItemNoResult() {
		// Open Search Catalog Page
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				getUrl() + "/catalog/searchForm.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Item : Search</title>"));

		// Search for an item
		resultEntity = restTemplate.getForEntity(
				getUrl() + "catalog/searchByName.html?query=abc&submit=", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("No items"));
	}

	@Test
	public void placeOrder() {
		// Open Order Page
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(getUrl() + "/order", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Order : View all</title>"));

		// Navigate to Add Order Page
		resultEntity = restTemplate.getForEntity(getUrl() + "/order/form.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Order : Add</title>"));
		assertTrue(resultEntity.getBody().contains("Rod Johnson"));

		//Request to add New Item to Cart
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("customerId", "2");
		map.add("addLine", "");
		resultEntity = restTemplate.postForEntity(getUrl() + "/order/line", map, String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<input type=\"text\" id=\"orderLine0.count\" name=\"orderLine[0].count\" value=\"0\" />"));
		assertTrue(resultEntity.getBody().contains("<option value=\"1\" selected=\"selected\" >iPod</option>"));

		// Add first item, item name = "iPod touch" and count=3
		map = new LinkedMultiValueMap<String, String>();
		map.add("customerId", "2");
		map.add("orderLine[0].count", "3");
		map.add("orderLine[0].itemId", "2");
		map.add("addLine", "");
		resultEntity = restTemplate.postForEntity(getUrl() + "/order/line", map, String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<option value=\"2\" selected=\"selected\" >iPod touch</option>"));
		assertTrue(resultEntity.getBody().contains("<input type=\"text\" id=\"orderLine0.count\" name=\"orderLine[0].count\" value=\"3\" />"));
		assertTrue(resultEntity.getBody().contains("<input type=\"text\" id=\"orderLine1.count\" name=\"orderLine[1].count\" value=\"0\" />"));

		// Submit Order
		map = new LinkedMultiValueMap<String, String>();
		map.add("customerId", "2");
		map.add("orderLine[0].count", "3");
		map.add("orderLine[0].itemId", "2");
		map.add("orderLine[1].count", "1");
		map.add("orderLine[1].itemId", "3");
		map.add("submit", "");
		resultEntity = restTemplate.postForEntity(getUrl() + "/order", map, String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Success</title>"));

		//Check if Order is Placed
		resultEntity = restTemplate.getForEntity(getUrl() + "/order", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Order : View all</title>"));
		assertTrue(resultEntity.getBody().contains("<td><a href=\"1\">1</a></td>"));

		//Check details of placed order
		resultEntity = restTemplate.getForEntity(getUrl() + "/order/1", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<title>Order</title>"));
		assertTrue(resultEntity.getBody().contains("Rod Johnson"));
		assertTrue(resultEntity.getBody().contains("Total price"));
		assertTrue(resultEntity.getBody().contains("iPod touch"));
		assertTrue(resultEntity.getBody().contains("iPod nano"));
	}
}
