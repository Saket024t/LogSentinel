package com.logsentinel.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IngestionService {

    private static final Logger logger = LoggerFactory.getLogger(IngestionService.class);

    private final ServiceMetadataRepository metadataRepository;
    private final LogDocumentRepository logDocumentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public IngestionService(ServiceMetadataRepository metadataRepository,
                            LogDocumentRepository logDocumentRepository,
                            KafkaTemplate<String, String> kafkaTemplate,
                            ObjectMapper objectMapper) {
        this.metadataRepository = metadataRepository;
        this.logDocumentRepository = logDocumentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "logs.raw", groupId = "ingestion-group")
    public void consume(String rawLog) {
        try {
            logger.info("Received raw log: {}", rawLog);
            JsonNode logNode = objectMapper.readTree(rawLog);

            String serviceName = logNode.path("service_name").asText(null);
            if (serviceName == null) {
                logger.warn("Log missing service_name: {}", rawLog);
                return;
            }

            String teamOwner = "Unknown";
            String criticality = "Low";
            Optional<ServiceMetadata> metadataOpt = metadataRepository.findByServiceName(serviceName);
            if (metadataOpt.isPresent()) {
                ServiceMetadata metadata = metadataOpt.get();
                teamOwner = metadata.getTeamOwner();
                criticality = metadata.getCriticality();
            }

            if (logNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) logNode;
                objectNode.put("team_owner", teamOwner);
                objectNode.put("criticality", criticality);
            }

            String enrichedLogStr = objectMapper.writeValueAsString(logNode);

            LogDocument logDoc = new LogDocument();
            logDoc.setMessage(logNode.path("message").asText());
            logDoc.setServiceName(serviceName);
            logDoc.setLevel(logNode.path("level").asText());
            logDoc.setTimestamp(logNode.path("@timestamp").asText());
            logDoc.setTeamOwner(teamOwner);
            logDoc.setCriticality(criticality);
            logDoc.setRawJson(enrichedLogStr);

            logDocumentRepository.save(logDoc);
            logger.info("Saved log to Elasticsearch");

            kafkaTemplate.send("logs.parsed", enrichedLogStr);
            logger.info("Published enriched log to logs.parsed");

        } catch (Exception e) {
            logger.error("Error processing log: {}", rawLog, e);
        }
    }
}
