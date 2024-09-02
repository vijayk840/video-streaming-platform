package com.example.transcodeservice.controller;

import com.example.transcodeservice.service.S3Service;
import com.example.transcodeservice.service.TranscoderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class TranscoderController {
    private final TranscoderService transcoderService;
    public TranscoderController(TranscoderService transcoderService, S3Service s3Service) {
        this.transcoderService = transcoderService;
    }

    @PostMapping("/transcode")
    public String transcode()
    {
        try{
            transcoderService.transcodeVideoToHLS("test.mp4");
            return "done";
        }
        catch(Exception e){
            e.printStackTrace();
            return "failed";
        }


    }
}
