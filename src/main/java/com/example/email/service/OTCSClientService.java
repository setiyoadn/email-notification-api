package com.example.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OTCSClientService {

    @Value("${otcs.base-url}")
    private String baseUrl;

    @Value("${otcs.username}")
    private String username;

    @Value("${otcs.password}")
    private String password;

    @Value("${otcs.report-node-id}")
    private String reportNodeId;

    private final RestTemplate restTemplate = new RestTemplate();

    public String loginAndGetTicket() {
        String url = baseUrl + "/auth";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> form = new HashMap<>();
        form.put("username", username);
        form.put("password", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("ticket");
        } else {
            throw new RuntimeException("Login to OTCS failed");
        }
    }

    public List<Map<String, Object>> runLiveReport() {
        String otcsticket = loginAndGetTicket();
        String url = baseUrl + "/nodes/" + reportNodeId + "/output";

        HttpHeaders headers = new HttpHeaders();
        headers.set("OTCSTicket", otcsticket);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, request, Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body == null || !body.containsKey("data")) {
            throw new RuntimeException("No data from LiveReport");
        }

        return (List<Map<String, Object>>) body.get("data");
    }
}
