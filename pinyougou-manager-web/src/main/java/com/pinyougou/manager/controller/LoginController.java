package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping
@RestController
public class LoginController {

    @RequestMapping("/login")
    public Map<String,String> login() {
        //获取当前登录的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //构建map对象
        Map<String, String> map = new HashMap<>();
        //将名称加入map数组中
        map.put("name", name);
        //返回map
        return map;
    }
}

