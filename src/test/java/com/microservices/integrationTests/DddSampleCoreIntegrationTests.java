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
public class DddSampleCoreIntegrationTests {

    private RestTemplate restTemplate = new RestTemplate();
    private Integer serverPort = 8080;

    private String getUrl() {
        return "http://localhost:" + serverPort + "/dddsample";
    }

    @BeforeEach
    public void loadHomePage(){
        // Home Page Load
        ResponseEntity<String> resultEntity = restTemplate.getForEntity(
                getUrl(), String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<title>DDDSample</title>"));
    }

    @Test
    public void trackPackage() {
        // Open Tracking Page
        ResponseEntity<String> resultEntity = restTemplate.getForEntity(
                getUrl() + "/track", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<title>Tracking cargo</title>"));
        assertTrue(resultEntity.getBody().contains("Enter your tracking id:"));

        // Track Package
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("trackingId", "ABC123");
        resultEntity = restTemplate.postForEntity(getUrl() + "/track", map,
                String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("ABC123"));
        assertTrue(resultEntity.getBody().contains("In port New York"));
    }

    @Test
    public void trackInvalidPackage() {
        // Open Tracking Page
        ResponseEntity<String> resultEntity = restTemplate.getForEntity(
                getUrl() + "/track", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<title>Tracking cargo</title>"));
        assertTrue(resultEntity.getBody().contains("Enter your tracking id:"));

        // Track Package
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("trackingId", "ABC1234");
        resultEntity = restTemplate.postForEntity(getUrl() + "/track", map,
                String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<p class=\"error\">Unknown tracking id</p>"));
    }

    @Test
    public void checkExistingCargoDetails() {
        // Open Cargo Page
        ResponseEntity<String> resultEntity = restTemplate.getForEntity(
                getUrl() + "/admin/list", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<title>Cargo Administration</title>"));
        assertTrue(resultEntity.getBody().contains("ABC123"));
        assertTrue(resultEntity.getBody().contains("JKL567"));

        // Get Cargo Details
        resultEntity = restTemplate.getForEntity(getUrl() + "/admin/show?trackingId=ABC123",
                String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("Details for cargo ABC123"));
    }

    @Test
    public void bookNewCargo() {
        // Open Cargo Page
        ResponseEntity<String> resultEntity = restTemplate.getForEntity(
                getUrl() + "/admin/list", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<title>Cargo Administration</title>"));
        assertTrue(resultEntity.getBody().contains("ABC123"));
        assertTrue(resultEntity.getBody().contains("JKL567"));

        // Open Book New Cargo Page
        resultEntity = restTemplate.getForEntity(getUrl() + "/admin/registration", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<caption>Book new cargo</caption>"));

        // Book New Cargo
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("originUnlocode", "DEHAM");
        map.add("destinationUnlocode", "FIHEL");
        map.add("arrivalDeadline", "22/02/2023");
        resultEntity = restTemplate.postForEntity(getUrl() + "/admin/register", map,
                String.class);
        String urlOfNewCargo = String.valueOf(resultEntity.getHeaders().getLocation());
        String trackingId = urlOfNewCargo.split("=")[1];
        assertTrue(resultEntity.getStatusCode().is3xxRedirection());

        //Get New Cargo Details
        resultEntity = restTemplate.getForEntity(urlOfNewCargo, String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<strong>Not routed</strong>"));
        assertTrue(resultEntity.getBody().contains(trackingId));
        assertTrue(resultEntity.getBody().contains("DEHAM"));
        assertTrue(resultEntity.getBody().contains("FIHEL"));
        assertTrue(resultEntity.getBody().contains("22/02/2023"));

        //Route Newly Booked Cargo
        resultEntity = restTemplate.getForEntity(getUrl() + "/admin/show?trackingId=" + trackingId, String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains(trackingId));

        //Check Cargo Page
        resultEntity = restTemplate.getForEntity(
                getUrl() + "/admin/list", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<title>Cargo Administration</title>"));
        assertTrue(resultEntity.getBody().contains("ABC123"));
        assertTrue(resultEntity.getBody().contains("JKL567"));
        assertTrue(resultEntity.getBody().contains(trackingId));
    }

    @Test
    public void changeDestinationOfCargo() {
        // Open Cargo Page
        ResponseEntity<String> resultEntity = restTemplate.getForEntity(
                getUrl() + "/admin/list", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<title>Cargo Administration</title>"));
        assertTrue(resultEntity.getBody().contains("ABC123"));
        assertTrue(resultEntity.getBody().contains("JKL567"));

        // Open Book New Cargo Page
        resultEntity = restTemplate.getForEntity(getUrl() + "/admin/registration", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<caption>Book new cargo</caption>"));

        // Book New Cargo
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("originUnlocode", "DEHAM");
        map.add("destinationUnlocode", "FIHEL");
        map.add("arrivalDeadline", "22/02/2023");
        resultEntity = restTemplate.postForEntity(getUrl() + "/admin/register", map,
                String.class);
        String urlOfNewCargo = String.valueOf(resultEntity.getHeaders().getLocation());
        String trackingId = urlOfNewCargo.split("=")[1];
        assertTrue(resultEntity.getStatusCode().is3xxRedirection());

        //Get New Cargo Details
        resultEntity = restTemplate.getForEntity(urlOfNewCargo, String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<strong>Not routed</strong>"));
        assertTrue(resultEntity.getBody().contains(trackingId));
        assertTrue(resultEntity.getBody().contains("DEHAM"));
        assertTrue(resultEntity.getBody().contains("FIHEL"));
        assertTrue(resultEntity.getBody().contains("22/02/2023"));

        //Change Destination of Cargo
        map = new LinkedMultiValueMap<String, String>();
        map.add("trackingId", trackingId);
        map.add("unlocode", "CNHGH");
        resultEntity = restTemplate.postForEntity(getUrl() + "/admin/changeDestination", map, String.class);
        assertTrue(resultEntity.getStatusCode().is3xxRedirection());

        //Get New Cargo Details with updated destination
        resultEntity = restTemplate.getForEntity(urlOfNewCargo, String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<strong>Not routed</strong>"));
        assertTrue(resultEntity.getBody().contains(trackingId));
        assertTrue(resultEntity.getBody().contains("DEHAM"));
        assertTrue(resultEntity.getBody().contains("CNHGH"));
        assertTrue(resultEntity.getBody().contains("22/02/2023"));
    }
}
