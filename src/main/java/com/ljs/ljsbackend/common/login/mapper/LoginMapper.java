package com.ljs.ljsbackend.common.login.mapper;

import com.ljs.ljsbackend.common.login.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LoginMapper {
    List<Map<String,Object>> getList();

    UserDto getUser(UserDto param);
}
