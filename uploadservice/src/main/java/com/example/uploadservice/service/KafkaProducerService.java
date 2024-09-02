package com.example.uploadservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);
    private KafkaTemplate<String,String> kafkaTemplate;
    public KafkaProducerService( KafkaTemplate<String,String> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
    }

    public void sendMessage(String message){
        LOGGER.info(String.format("Message sent",message));
        kafkaTemplate.send("transcode",message);
    }
}
