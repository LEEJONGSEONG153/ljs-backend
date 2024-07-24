package com.ljs.ljsbackend.common.menu.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MenuMapper {
    List<Map<String,Object>> getList();

    void update(Map<String, Object> menu);

    void insert(Map<String, Object> menu);
}
