package com.dansam0.steamdemo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "profiles")
@Data
public class Profile {

    @Id
    @Column(name = "steam_id")
    private long steamId;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "last_update_time")
    private Timestamp lastUpdateTime;

    @Column(name = "creation_time")
    private Timestamp creationTime;

    @Column(name = "overall_playtime")
    private int overallPlaytime = 0;

}
