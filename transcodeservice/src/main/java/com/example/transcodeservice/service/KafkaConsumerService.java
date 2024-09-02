package com.example.transcodeservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger LOGGER=LoggerFactory.getLogger(KafkaConsumerService.class);
    private final TranscoderService transcoderService;

    public KafkaConsumerService(TranscoderService transcoderService) {
        this.transcoderService = transcoderService;
    }

    //subscriber method
    @KafkaListener(topics="transcode",groupId="myGroup")
    public void consume(String Key)
    {
        LOGGER.info(String.format("Received Key: %s", Key));
        try{
            transcoderService.transcodeVideoToHLS(Key);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }


    }
}
