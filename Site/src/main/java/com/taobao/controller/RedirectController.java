package com.taobao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RedirectController {
    @RequestMapping("/loginPage")
    public String loginPage(){
        return "fore/login";
    }
    @RequestMapping("/registerPage")
    public String registerPage(){
        return "fore/register";
    }
    @RequestMapping("cart")
    public String cart(){
        return "fore/cart";
    }
    @RequestMapping("/alipay")
    public ModelAndView alipay(Integer oid,Float total){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("oid",oid);
        modelAndView.addObject("total",total);
        modelAndView.setViewName("fore/alipay");
        return modelAndView;
    }
}
