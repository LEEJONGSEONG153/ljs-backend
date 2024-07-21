package com.ljs.ljsbackend.common.login.controller;

import com.ljs.ljsbackend.common.login.dto.UserDto;
import com.ljs.ljsbackend.common.login.service.LoginService;
import com.ljs.ljsbackend.common.menu.service.MenuService;
import com.ljs.ljsbackend.response.DefaultRes;
import com.ljs.ljsbackend.response.ResponseMessage;
import com.ljs.ljsbackend.response.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final MenuService menuService;


    @PostMapping("/api/v1/login")
    public ResponseEntity<Object> login(@RequestBody UserDto userDto) {

        //todo 로그인
        UserDto userInfo = loginService.getUser(userDto);
        if(userInfo != null) {
            //todo 로그인 성공시 메뉴 리스트 조회
            Map<String,Object> map = new HashMap<>();
            List<Map<String, Object>> menuList = menuService.getList();
            map.put("userInfo",userInfo);
            map.put("menuList",menuList);

            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.CONNECT_SUCCESS, map), HttpStatus.OK);
        }
        return null;
    }
}
