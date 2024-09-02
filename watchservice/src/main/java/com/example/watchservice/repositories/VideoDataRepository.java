package com.example.watchservice.repositories;

import com.example.watchservice.model.VideoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoDataRepository extends JpaRepository<VideoData,Integer> {
}
