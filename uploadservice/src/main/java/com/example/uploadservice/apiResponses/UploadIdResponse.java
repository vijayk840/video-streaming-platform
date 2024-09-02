package com.example.uploadservice.apiResponses;

public class UploadIdResponse {
    private String uploadId;

    public UploadIdResponse(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
