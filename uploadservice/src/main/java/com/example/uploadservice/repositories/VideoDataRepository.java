package com.example.uploadservice.repositories;

import com.example.uploadservice.models.VideoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoDataRepository extends JpaRepository<VideoData,Integer> {
}
