package com.ljs.ljsbackend.common.menu.controller;

import com.ljs.ljsbackend.common.menu.service.MenuService;
import com.ljs.ljsbackend.response.DefaultRes;
import com.ljs.ljsbackend.response.ResponseMessage;
import com.ljs.ljsbackend.response.StatusCode;
import com.ljs.ljsbackend.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MenuController {

    private final MenuService service;

    @GetMapping("/api/v1/menu")
    public ResponseEntity<Object> getMenuList() {

        List<Map<String, Object>> list = service.getMenuList();
        return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.CONNECT_SUCCESS, list), HttpStatus.OK);
    }
    @PostMapping("/api/v1/menu")
    public ResponseEntity<Object> updateMenu(@RequestBody Map<String,Object> mapParam) {

        List<Map<String,Object>> menus = (ArrayList<Map<String,Object>>)mapParam.get("Menus");
        
        for(int i=0; i<menus.size(); i++) {
            Map<String, Object> menu = menus.get(i);
            if(ObjectUtils.isEmpty(menu.get("menuId"))){

                //인서트
                System.out.println("인서트");
                String menuCd = (String) menu.get("menuCd");
                menu.put("menuId",menuCd.substring(0,1));
                if(menuCd.length() >= 1) {
                    menu.put("menuLevel",0);
                    menu.put("menuCd",menuCd.substring(0,1));
                    menu.put("parentMenuCd",null);
                    service.insertMenu(menu);
                }
                if(menuCd.length() >= 3) {
                    menu.put("menuLevel",1);
                    menu.put("menuCd",menuCd.substring(0,3));
                    menu.put("parentMenuCd",menuCd.substring(0,1));
                    service.insertMenu(menu);
                }
                if(menuCd.length() >= 5) {
                    menu.put("menuLevel",2);
                    menu.put("menuCd",menuCd.substring(0,5));
                    menu.put("parentMenuCd",menuCd.substring(0,3));
                    service.insertMenu(menu);
                }
                if(menuCd.length() >= 7) {
                    menu.put("menuLevel",3);
                    menu.put("menuCd",menuCd.substring(0,7));
                    menu.put("parentMenuCd",menuCd.substring(0,5));
                    service.insertMenu(menu);
                }

            } else {
                //업데이트
                System.out.println("업데이트");
               service.updateMenu(menu);
            }
        }

        //List<Map<String, Object>> list = service.getList();
        return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.CONNECT_SUCCESS, null), HttpStatus.OK);
    }
    @DeleteMapping("/api/v1/menu")
    public ResponseEntity<Object> deleteMenu(@RequestBody Map<String,Object> mapParam) {

        ArrayList list = (ArrayList)mapParam.get("params");
        Map<String,Object> map = new HashMap<>();
        for(int i=0; i<list.size(); i++) {
            String menuCd = (String)list.get(i);
            map.put("menuCd",menuCd);
            service.deleteMenu(map);
            map.clear();
        }
        return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.CONNECT_SUCCESS, null), HttpStatus.OK);
    }



}
