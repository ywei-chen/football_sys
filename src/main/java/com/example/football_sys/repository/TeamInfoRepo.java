package com.example.football_sys.repository;

import com.example.football_sys.entity.TeamInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamInfoRepo extends JpaRepository<TeamInfo, Long> {
}