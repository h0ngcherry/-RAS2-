package com.example.documentReview.domain;

import lombok.Data;

import java.io.InputStream;
import java.util.Date;

@Data
public class ApprFile {

    private int id;
    private String userName;
    private String fileName;
    private String fileSm;
    private Date craDate;
    private Date upDate;
    private String remark;
    private String title;
    private String filePath;
    private String status; //0：待审批 1：审批通过 2：审批未通过
    private String statusMsg;
    private String craDateStr;
    private String upDataStr;
}
