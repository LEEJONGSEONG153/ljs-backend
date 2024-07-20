package com.ljs.ljsbackend.common.menu.controller;

import com.ljs.ljsbackend.common.menu.service.MenuService;
import com.ljs.ljsbackend.response.DefaultRes;
import com.ljs.ljsbackend.response.ResponseMessage;
import com.ljs.ljsbackend.response.StatusCode;
import com.ljs.ljsbackend.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/api/v1/menu/list")
    public ResponseEntity<Object> testApi() {

        List<Map<String, Object>> list = menuService.getList();
        return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.CONNECT_SUCCESS, list), HttpStatus.OK);
    }
}
