package com.logsentinel.anomaly;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AnomalyConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(AnomalyConsumerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String aiEngineUrl;

    public AnomalyConsumerService(KafkaTemplate<String, String> kafkaTemplate,
                                  RestTemplate restTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${ai-engine.url}") String aiEngineUrl) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.aiEngineUrl = aiEngineUrl;
    }

    @KafkaListener(topics = "logs.parsed", groupId = "anomaly-group")
    public void consume(String parsedLog) {
        try {
            logger.info("Received parsed log: {}", parsedLog);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(parsedLog, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(aiEngineUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Boolean isAnomaly = (Boolean) response.getBody().get("anomaly");

                if (Boolean.TRUE.equals(isAnomaly)) {
                    logger.warn("Anomaly detected! Forwarding to logs.anomaly...");
                    kafkaTemplate.send("logs.anomaly", parsedLog);
                }
            } else {
                logger.error("AI engine returned non-success response: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error processing log for anomaly detection", e);
        }
    }
}
