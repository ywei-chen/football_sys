package com.example.football_sys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Audited;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "division"}))
public class TeamInfo {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String division;
    private String phone;
    private String email;
}