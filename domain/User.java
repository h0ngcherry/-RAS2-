package com.example.documentReview.domain;

import lombok.Data;

import java.util.Date;

@Data
public class User extends BaseObject {
	private int id;
	private String userName;
	private String password;
	private String email;
	private Date addDate;
	private Date updateDate;
	private int state; // 1 正常 2 冻结
	private String stateStr;
	private String limitFlag; //权限 0管理员 1审批员 2用户
	private String limitFlagStr; //权限 0管理员 1审批员 2用户

	private String addDateStr;
	private String updateDateStr;
}
