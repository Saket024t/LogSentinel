package com.logsentinel.alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AlertConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AlertConsumer.class);

    @KafkaListener(topics = "logs.anomaly", groupId = "alert-group")
    public void consumeAlert(String anomalyLog) {
        logger.info("================ ALERT NOTIFICATION ================");
        logger.info("Anomaly Detected in system!");
        logger.info("Details: {}", anomalyLog);
        logger.info("=====================================================");
        // In a real application, this is where we would trigger an email or Slack webhook.
    }
}
