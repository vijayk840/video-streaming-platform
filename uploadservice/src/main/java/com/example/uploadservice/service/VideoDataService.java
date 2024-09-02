package com.example.uploadservice.service;

import com.example.uploadservice.models.VideoData;
import com.example.uploadservice.repositories.VideoDataRepository;
import org.springframework.stereotype.Service;

@Service
public class VideoDataService {
    private final VideoDataRepository videoDataRepository;
    public VideoDataService(VideoDataRepository videoDataRepository) {
        this.videoDataRepository = videoDataRepository;
    }

    public VideoData saveVideoDataToDb(VideoData videoData)
    {
        return videoDataRepository.save(videoData);
    }

}
