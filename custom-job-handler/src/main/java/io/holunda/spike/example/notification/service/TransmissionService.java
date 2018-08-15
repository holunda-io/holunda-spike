package io.holunda.spike.example.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class TransmissionService {

  private static final Logger logger = LoggerFactory.getLogger(TransmissionService.class);

  public void transmitNotification(String recipient, String subject, String body) {
    logger.info("Transmitting the message to '{}' with subject '{}' and body '{}'", recipient, subject, body);
  }
}
