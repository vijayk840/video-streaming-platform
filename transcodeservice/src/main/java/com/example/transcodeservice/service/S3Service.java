package com.example.transcodeservice.service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
@Service
public class S3Service {
    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public void downloadFileFromS3(String key,String localFilePath){
        File file = new File(localFilePath);
        if (!file.exists()) {
            try{
                Files.createFile(file.toPath());
            }catch(Exception e)
            {
                System.out.println("error creating file");
            }

        }
        s3Client.getObject(new GetObjectRequest(bucketName, key), file);
        System.out.println("Downloaded file from S3: " + localFilePath);
    }
    public void uploadFileToS3AndDeleteLocally(File file) throws IOException {
        String key = file.getName();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        s3Client.putObject(putObjectRequest);
        System.out.println("Uploaded file to S3: " + key);
        Files.delete(file.toPath());
        System.out.println("Deleted local file: " + file.getPath());
    }


    public String getMasterPlaylistFileUrl(String key) {
        return s3Client.getUrl(bucketName, key).toString();
    }
}
