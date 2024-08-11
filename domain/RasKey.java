package com.example.documentReview.domain;

import lombok.Data;

import java.util.Date;

@Data
public class RasKey {
    private int id;
    private String userName;
    private String pubKey;
    private String priKey;
    private Date craDate;
    private String craDateStr;
}
