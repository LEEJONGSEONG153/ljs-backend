package com.ljs.ljsbackend.common.file.mapper;

import com.ljs.ljsbackend.common.file.dto.FileDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FileMapper {

    void save(Map<String, Object> map);

    List<FileDto> getList(Map<String,Object> mpa);
}
