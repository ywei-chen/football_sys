package com.example.football_sys.controller;
import com.example.football_sys.entity.TeamInfo;
import com.example.football_sys.service.TeamInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class TeamInfoController {
    private final TeamInfoService teamInfoService;
    public TeamInfoController(TeamInfoService teamInfoService){ this.teamInfoService = teamInfoService; }

    @GetMapping("/filterTeams")
    public List<TeamInfo> filterTeams(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email
    ) {
        return teamInfoService.filterTeam(name, division, phone, email);
    }

    @GetMapping("/allTeams")
    public List<TeamInfo> getTeams() { return teamInfoService.getAllTeam(); }

    @PostMapping("/addTeams")
    public ResponseEntity<String> addTeams(@RequestBody TeamInfo teamInfo) {
        return switch (teamInfoService.createTeam(teamInfo)) {
            case SUCCESS -> ResponseEntity.ok().body("新增成功");
            case DUPLICATE -> ResponseEntity.badRequest().body("球隊重複");
            case FAILED -> ResponseEntity.internalServerError().body("新增失敗");
        };
    }

    @PostMapping("/importTeams")
    public ResponseEntity<String> excelAddTeams(@RequestParam("file") MultipartFile file) {
        return switch (teamInfoService.excelAddTeam(file)) {
            case SUCCESS -> ResponseEntity.ok().body("新增成功");
            case DUPLICATE -> ResponseEntity.badRequest().body("球隊重複");
            case FAILED -> ResponseEntity.badRequest().body("新增失敗");
        };
    }
}
