package com.example.paymentmapping.controller;
import com.example.paymentmapping.entity.TeamInfo;
import com.example.paymentmapping.service.TeamInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeamInfoController {
    private final TeamInfoService teamInfoService;
    public TeamInfoController(TeamInfoService teamInfoService){ this.teamInfoService = teamInfoService; }

    @GetMapping("/teams")
    public List<TeamInfo> getTeams() { return teamInfoService.getAllTeam(); }
}
