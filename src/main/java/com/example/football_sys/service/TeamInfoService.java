package com.example.football_sys.service;
import com.example.football_sys.repository.TeamInfoRepo;
import com.example.football_sys.entity.TeamInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Example;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class TeamInfoService {
    private final TeamInfoRepo teamInfoRepo;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public TeamInfoService(TeamInfoRepo teamInfoRepo, NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.teamInfoRepo = teamInfoRepo;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    // 取得特定搜尋資料
    public List<TeamInfo> filterTeam(String name, String division, String phone, String email) {
        TeamInfo raw = new TeamInfo();
        raw.setName(name);
        raw.setDivision(division);
        raw.setPhone(phone);
        raw.setEmail(email);

        return teamInfoRepo.findAll(Example.of(raw));
    }

    // 取得所有球隊基本資料
    public List<TeamInfo> getAllTeam() {
        return this.teamInfoRepo.findAll();
    }

    // 新增單筆球隊資訊
    public boolean createTeam(TeamInfo teamInfo) {
        try {
            TeamInfo res = this.teamInfoRepo.save(teamInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Excel匯入資料庫功能
    public boolean excelAddTeam(MultipartFile file) {
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if(row.getRowNum() == 0) continue;
                TeamInfo data = new TeamInfo();
                data.setName(row.getCell(0).getStringCellValue());
                data.setDivision(row.getCell(1).getStringCellValue());
                data.setPhone(row.getCell(2).getStringCellValue());
                data.setEmail(row.getCell(3).getStringCellValue());
                createTeam(data);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
