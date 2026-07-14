package com.example.football_sys.controller;
import com.example.football_sys.entity.TeamInfo;
import com.example.football_sys.service.TeamInfoService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class TeamInfoController {
    private final TeamInfoService teamInfoService;
    public TeamInfoController(TeamInfoService teamInfoService){ this.teamInfoService = teamInfoService; }

    @GetMapping("filterTeams")
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
    public ResponseEntity<Void> addTeams(@RequestBody TeamInfo teamInfo) {
        boolean success = teamInfoService.createTeam(teamInfo);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/importTeams")
    public ResponseEntity<Void> excelAddTeams(MultipartFile file) {
        boolean sucess = teamInfoService.excelAddTeam(file);
        return sucess ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
