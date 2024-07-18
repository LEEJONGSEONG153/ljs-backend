package com.ljs.ljsbackend.service;

import com.ljs.ljsbackend.mapper.TestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestMapper testMapper;

    public List<Map<String, Object>> getList () {

        List<Map<String, Object>> list = testMapper.getList();
        return list;
    }


}
