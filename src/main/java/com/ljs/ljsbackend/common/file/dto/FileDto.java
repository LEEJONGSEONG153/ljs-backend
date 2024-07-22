package com.ljs.ljsbackend.common.file.dto;

import lombok.Data;

@Data
public class FileDto {
    private String fileId;
    private String fileSeq;
    private String fileNm;
    private String filePath;
    private String attr1;
    private String attr2;
    private String insertId;
    private String isnertDate;
    private String udpateId;
    private String udpateDate;
}
