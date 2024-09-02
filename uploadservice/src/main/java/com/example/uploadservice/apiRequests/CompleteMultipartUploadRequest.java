package com.example.uploadservice.apiRequests;
import java.util.List;
public class CompleteMultipartUploadRequest {
    private String fileName;
    private String uploadId;
    private List<String> eTags;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public List<String> geteTags() {
        return eTags;
    }

    public void seteTags(List<String> eTags) {
        this.eTags = eTags;
    }
}
