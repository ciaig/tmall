package com.taobao.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.taobao.pojo.User;
import com.taobao.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @Reference
    private UserService userService;
    @RequestMapping("/login")
    public ModelAndView login(User user, HttpSession session){
        ModelAndView modelAndView = new ModelAndView();
        User login = userService.login(user);
        if(login!=null){
            session.setAttribute("user",login);
            modelAndView.setViewName("redirect:home");
        }else{
            modelAndView.addObject("msg","用户名或密码错误");
            modelAndView.setViewName("fore/login");
        }
        return modelAndView;
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.setAttribute("user",null);
        session.setAttribute("ois",null);
        return "redirect:home";
    }
    @RequestMapping("/register")
    public ModelAndView register(User user){
        ModelAndView modelAndView = new ModelAndView();
        if(userService.register(user)){
            modelAndView.setViewName("fore/registerSuccess");
        }else{
            modelAndView.addObject("msg","用户名已存在");
            modelAndView.setViewName("fore/register");
        }
        return modelAndView;
    }
    @RequestMapping("/checkLogin")
    @ResponseBody
    public String checkLogin(HttpSession session){
        if(session.getAttribute("user")!=null){
            return "success";
        }else{
            return "failed";
        }
    }
    @RequestMapping("/loginAjax")
    @ResponseBody
    public String loginAjax(User user,HttpSession session){
        User login = userService.login(user);
        if(login!=null){
            session.setAttribute("user",login);
            return "success";
        }else{
            return "failed";
        }
    }
}
