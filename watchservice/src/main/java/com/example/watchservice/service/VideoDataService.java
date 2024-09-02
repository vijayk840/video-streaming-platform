package com.example.watchservice.service;

import com.example.watchservice.model.VideoData;
import com.example.watchservice.repositories.VideoDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoDataService {
    private final VideoDataRepository videoDataRepository;

    public VideoDataService(VideoDataRepository videoDataRepository) {
        this.videoDataRepository = videoDataRepository;
    }

    public List<VideoData> getAllVideos() {
        return videoDataRepository.findAll();
    }
}
