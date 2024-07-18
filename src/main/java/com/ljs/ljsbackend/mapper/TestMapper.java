package com.ljs.ljsbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestMapper {
    List<Map<String,Object>> getList();
}
