package com.example.football_sys.controller;
import com.example.football_sys.entity.TeamInfo;
import com.example.football_sys.service.TeamInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeamInfoController {
    private final TeamInfoService teamInfoService;
    public TeamInfoController(TeamInfoService teamInfoService){ this.teamInfoService = teamInfoService; }

    @GetMapping("/teams")
    public List<TeamInfo> getTeams() { return teamInfoService.getAllTeam(); }

    @PostMapping("/addTeam")
    public ResponseEntity<Void> addTeam(@RequestBody TeamInfo teamInfo) {
        boolean created = teamInfoService.createTeam(teamInfo);
        return created ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
