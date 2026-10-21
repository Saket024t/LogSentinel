package com.logsentinel.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngestionServiceTest {

    @Mock
    private ServiceMetadataRepository metadataRepository;

    @Mock
    private LogDocumentRepository logDocumentRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper;
    private IngestionService ingestionService;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        ingestionService = new IngestionService(metadataRepository, logDocumentRepository, kafkaTemplate, objectMapper);
    }

    @Test
    public void testConsumeEnrichesAndPublishesLog() {
        String rawLog = "{\"message\":\"Test log\",\"level\":\"INFO\",\"@timestamp\":\"2023-10-01T12:00:00.000Z\",\"service_name\":\"order-service\"}";

        ServiceMetadata metadata = new ServiceMetadata();
        metadata.setServiceName("order-service");
        metadata.setTeamOwner("commerce-team");
        metadata.setCriticality("High");

        when(metadataRepository.findByServiceName("order-service")).thenReturn(Optional.of(metadata));

        ingestionService.consume(rawLog);

        ArgumentCaptor<LogDocument> docCaptor = ArgumentCaptor.forClass(LogDocument.class);
        verify(logDocumentRepository, times(1)).save(docCaptor.capture());
        LogDocument savedDoc = docCaptor.getValue();

        assertEquals("Test log", savedDoc.getMessage());
        assertEquals("order-service", savedDoc.getServiceName());
        assertEquals("commerce-team", savedDoc.getTeamOwner());
        assertEquals("High", savedDoc.getCriticality());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq("logs.parsed"), kafkaCaptor.capture());
        String publishedLog = kafkaCaptor.getValue();

        assertTrue(publishedLog.contains("\"team_owner\":\"commerce-team\""));
        assertTrue(publishedLog.contains("\"criticality\":\"High\""));
    }
}
