package com.example.watchservice.controller;

import com.example.watchservice.apiResponse.PresignedUrlResponse;
import com.example.watchservice.model.VideoData;
import com.example.watchservice.service.S3Service;
import com.example.watchservice.service.VideoDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/watch")
public class WatchController {

    private final VideoDataService videoDataService;
    private final S3Service s3Service;
    public WatchController(VideoDataService videoDataService, S3Service s3Service) {
        this.videoDataService = videoDataService;
        this.s3Service = s3Service;
    }

    @GetMapping("/getHealth")
    public String healthCheck(){
        return "UP";
    }

    @GetMapping("/getAllVideos")
    public ResponseEntity<?> getAllVideos()
    {
        try{
            List<VideoData>videoData=videoDataService.getAllVideos();
            Map<String,Object> response=new HashMap<>();
            response.put("message","Fetched Succesfully");
            response.put("data",videoData);

            return ResponseEntity.status(200).body(response);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching the videos");
        }
    }

    @GetMapping("/getVideo")
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(@RequestParam("key") String key){
        String hlsSupportedKey=key+"_master.m3u8";
        String presignedUrl=s3Service.generatePresignedUrl(key);
        PresignedUrlResponse presignedUrlResponse=new PresignedUrlResponse();
        presignedUrlResponse.setPresignedUrl(presignedUrl);
        return ResponseEntity.ok(presignedUrlResponse);
    }
}
