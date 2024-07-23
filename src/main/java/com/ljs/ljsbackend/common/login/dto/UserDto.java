package com.ljs.ljsbackend.common.login.dto;

import lombok.Data;

@Data
public class UserDto {
    private String userId;
    private String userNM;
    private String userPhone;
    private String insertId;
    private String insertDate;
    private String updateId;
    private String updateDate;
}
