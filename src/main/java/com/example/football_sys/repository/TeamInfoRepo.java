package com.example.paymentmapping.repository;

import com.example.paymentmapping.entity.TeamInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamInfoRepo extends JpaRepository<TeamInfo, Long> {
}
