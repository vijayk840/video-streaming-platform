package com.example.uploadservice.apiResponses;

public class UploadChunkResponse {
    private String eTag;

    public UploadChunkResponse(String eTag) {
        this.eTag = eTag;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}
