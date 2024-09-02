package com.example.uploadservice.controller;

import com.example.uploadservice.apiRequests.CompleteMultipartUploadRequest;
import com.example.uploadservice.apiRequests.InitiateMultipartUploadRequest;
import com.example.uploadservice.apiResponses.ApiResponse;
import com.example.uploadservice.apiResponses.UploadChunkResponse;
import com.example.uploadservice.apiResponses.UploadIdResponse;
import com.example.uploadservice.models.VideoData;
import com.example.uploadservice.service.KafkaProducerService;
import com.example.uploadservice.service.S3Service;
import com.example.uploadservice.service.VideoDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadRestController {
    private final S3Service s3Service;
    private final KafkaProducerService kafkaProducer;
    private final VideoDataService videoDataService;
    public UploadRestController(S3Service s3Service, KafkaProducerService kafkaProducer, VideoDataService videoDataService) {
        this.s3Service = s3Service;
        this.kafkaProducer=kafkaProducer;
        this.videoDataService = videoDataService;
    }

    @GetMapping("/check")
    public String check(@RequestParam("message") String message)
    {
        return "done";
    }
    @PostMapping("/initiate")
    public ApiResponse initiateMultipartUpload(@RequestBody InitiateMultipartUploadRequest request){
        try{
            String uploadId=s3Service.initiateMultiPartUpload(request.getFileName());
            return new ApiResponse(200,"Success","MultiPartUpload",new UploadIdResponse(uploadId));
        }
        catch(Exception e){
            e.printStackTrace();
            return new ApiResponse(500,"error","Failed to initiate multipart Upload",null);
        }
    }

    @PostMapping("/uploadChunks")
    public ApiResponse uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("uploadId") String uploadId) {
        try {
            String eTag = s3Service.uploadChunk(
                    fileName,
                    chunkIndex,
                    uploadId,
                    chunk
            );
            return new ApiResponse(200, "Success", "Chunk Uploaded", new UploadChunkResponse(eTag));
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(500, "Error", "FailedToUploadChunk", null);
        }
    }

    @PostMapping("/completeUpload")
    public ApiResponse completeMultiPartUpload(
            @RequestBody CompleteMultipartUploadRequest request
    ){
        try{
            String videoUrl=s3Service.completeMultiPartUpload(request.getFileName(), request.getUploadId(), request.geteTags());
            videoDataService.saveVideoDataToDb(new VideoData(null,null,null,videoUrl));
            kafkaProducer.sendMessage(request.getFileName());
            return new ApiResponse(200,"Success","Upload Completed Successfully",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse(500,"Error","Failed To Complete upload",null);
        }
    }


}
