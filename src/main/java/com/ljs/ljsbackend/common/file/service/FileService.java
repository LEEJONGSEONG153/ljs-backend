package com.ljs.ljsbackend.common.file.service;

import com.ljs.ljsbackend.common.file.dto.FileDto;
import com.ljs.ljsbackend.common.file.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper mapper;

    public void save(Map<String, Object> map) {
        mapper.save(map);
    }

    public List<FileDto> getList() {
        return mapper.getList();

    }
}
