package com.ljs.ljsbackend.common.menu.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MenuMapper {
    List<Map<String,Object>> getMenuList();

    void updateMenu(Map<String, Object> menu);

    void insertMenu(Map<String, Object> menu);

    void deleteMenu(Map<String, Object> mapParam);
}
