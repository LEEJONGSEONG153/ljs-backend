package com.ljs.ljsbackend.common.login.service;

import com.ljs.ljsbackend.common.login.dto.UserDto;
import com.ljs.ljsbackend.common.login.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginMapper mapper;

    public UserDto getUser(UserDto param) {
        return mapper.getUser(param);
    }

    public List<Map<String, Object>> getList () {

        List<Map<String, Object>> list = mapper.getList();
        return list;
    }


}
