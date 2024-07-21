package com.ljs.ljsbackend.common.menu.service;

import com.ljs.ljsbackend.common.menu.mapper.MenuMapper;
import com.ljs.ljsbackend.mapper.TestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper mapper;

    public List<Map<String, Object>> getList () {

        List<Map<String, Object>> list = mapper.getList();
        return list;
    }


}
