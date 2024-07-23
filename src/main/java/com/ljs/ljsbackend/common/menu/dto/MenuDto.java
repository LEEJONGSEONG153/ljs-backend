package com.ljs.ljsbackend.common.menu.dto;

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
    private String insertDate;
    private String updateId;
    private String updateDate;
}
