package com.ljs.ljsbackend.controller;

import com.ljs.ljsbackend.response.DefaultRes;
import com.ljs.ljsbackend.response.ResponseMessage;
import com.ljs.ljsbackend.response.StatusCode;
import com.ljs.ljsbackend.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/hello")
    public ResponseEntity<Object> testApi() {

        List<Map<String, Object>> list = testService.getList();
        for(var i=0; i<5; i++) {
            list.add(list.get(0));
        }
        String result = "API 통신에 성공하였습니다.";
        System.out.println("here!");


        return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.CONNECT_SUCCESS, list), HttpStatus.OK);
    }
}
