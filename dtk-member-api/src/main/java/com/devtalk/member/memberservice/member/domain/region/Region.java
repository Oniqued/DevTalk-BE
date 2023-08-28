package com.devtalk.member.memberservice.member.domain.region;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Region {
    @Id
    @GeneratedValue
    @Column(name = "region_id")
    Long id;

    String region;
}
