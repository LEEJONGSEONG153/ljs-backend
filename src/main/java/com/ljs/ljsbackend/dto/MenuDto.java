package com.ljs.ljsbackend.dto;

import lombok.Data;

@Data
public class MenuDto {
    private String MenuId;
    private String MenuCd;
    private String MenuNm;
    private String MenuLevel;
    private String MenuPath;
    private String ParentMenuCd;
    private String remark;
    private String isUse;
    private String insertId;
    private String isnertDate;
    private String udpateId;
    private String udpateDate;
}
