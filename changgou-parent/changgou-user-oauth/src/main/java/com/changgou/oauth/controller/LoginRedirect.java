package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 展示页面
 * @author ljh
 * @version 1.0
 * @date 2020/10/11 08:57
 * @description 标题
 * @package com.changgou.oauth.controller
 */
@Controller
@RequestMapping("/oauth")
public class LoginRedirect {
    @GetMapping(value = "/login")
    public String showLogin(String url, Model model){
        model.addAttribute("url",url);
        return "login";
    }

}
