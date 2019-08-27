package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class LoginController {

    @RequestMapping("/loginName")
    public Map<String, String> loginName() {
        //定义空的map集合
        Map<String, String> map = new HashMap<>();
        //获取当前登录用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //将当前用户名加入到map集合中
        map.put("name", name);
        //返回map集合
        return map;
    }
}
