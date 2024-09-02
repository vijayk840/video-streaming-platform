package com.example.uploadservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);


    public String initiateMultiPartUpload(String fileName) {
        InitiateMultipartUploadRequest initiateRequest= new InitiateMultipartUploadRequest(bucketName,fileName);
        InitiateMultipartUploadResult initiateResult= s3client.initiateMultipartUpload(initiateRequest);
        return initiateResult.getUploadId();
    }


    public String uploadChunk(String fileName, int chunkIndex, String uploadId, MultipartFile chunk) throws Exception {
        int partNumber=chunkIndex+1;
        UploadPartRequest uploadRequest=new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(fileName)
                .withUploadId(uploadId)
                .withPartNumber(partNumber)
                .withInputStream(chunk.getInputStream())
                .withPartSize(chunk.getSize());
        UploadPartResult uploadResult=s3client.uploadPart(uploadRequest);
        return uploadResult.getPartETag().getETag();

    }


    //return url of uploaded video to amazonS3
    public String completeMultiPartUpload(String fileName, String uploadId, List<String> eTags) throws Exception {
        List<PartETag>partETags=new ArrayList<>();
        for(int i=0;i<eTags.size();i++)
        {
            partETags.add(new PartETag(i+1,eTags.get(i)));

        }

        CompleteMultipartUploadRequest completeRequest= new CompleteMultipartUploadRequest(
                bucketName,
                fileName,
                uploadId,
                partETags
        );
        CompleteMultipartUploadResult uploadResult = s3client.completeMultipartUpload(completeRequest);
        return uploadResult.getLocation();
    }


}