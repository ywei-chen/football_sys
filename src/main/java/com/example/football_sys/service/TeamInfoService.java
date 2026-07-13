package com.example.football_sys.service;
import com.example.football_sys.repository.TeamInfoRepo;
import com.example.football_sys.entity.TeamInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamInfoService {
    private final TeamInfoRepo teamInfoRepo;
    public TeamInfoService(TeamInfoRepo teamInfoRepo) {
        this.teamInfoRepo = teamInfoRepo;
    }

    // 取得所有球隊基本資料
    public List<TeamInfo> getAllTeam() {
        return this.teamInfoRepo.findAll();
    }

    // 新增球隊資訊至資料庫
    public boolean createTeam(TeamInfo teamInfo) {
        try {
            TeamInfo res = this.teamInfoRepo.save(teamInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
