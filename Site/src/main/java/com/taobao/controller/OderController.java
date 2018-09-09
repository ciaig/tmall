package com.taobao.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.taobao.pojo.*;
import com.taobao.service.OrderService;
import com.taobao.service.ProductsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class OderController {
    @Reference
    private OrderService orderService;
    @Reference
    private ProductsService productsService;
    @RequestMapping("/buy")
    public ModelAndView buy(Integer pid,Integer num,Boolean direct,HttpSession session){
        ModelAndView modelAndView = new ModelAndView();
        if(direct){
            OrderItem orderItem = new OrderItem();
            orderItem.setPid(pid);
            orderItem.setNumber(num);
            Product product = productsService.getProductByid(pid);
            orderItem.setProduct(product);
            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem);
            Float total=product.getPromotePrice()*num;
            modelAndView.addObject("total",total);
            modelAndView.addObject("ois",orderItems);
            modelAndView.addObject("pid",pid);
            modelAndView.addObject("num",num);
            modelAndView.addObject("direct",direct);
        }else{
            List<OrderItem> orderItems= (List<OrderItem>) session.getAttribute("ois");
            Float total=0F;
            for(OrderItem orderItem:orderItems){
                Product product = productsService.getProductByid(orderItem.getPid());
                total+=product.getPromotePrice()*orderItem.getNumber();
            }
            modelAndView.addObject("total",total);
            modelAndView.addObject("direct",direct);
        }
        modelAndView.setViewName("fore/buy");
        return modelAndView;
    }
    @RequestMapping("/addCart")
    @ResponseBody
    public String addCart(Integer pid, Integer num, HttpSession session){
        User user = (User) session.getAttribute("user");
        boolean flag=true;
        Object value = session.getAttribute("ois");
        List<OrderItem> ois=null;
        if(value==null){
            ois=new ArrayList<>();
        }else{
            ois= (List<OrderItem>) value;
        }
        for(OrderItem orderItem:ois){
            if(orderItem.getPid().intValue()==pid){
                orderItem.setNumber(orderItem.getNumber()+num);
                flag=false;
            }
        }
        if(flag){
            OrderItem orderItem = new OrderItem();
            Product product=productsService.getProductByid(pid);
            orderItem.setId(pid);
            orderItem.setPid(pid);
            orderItem.setNumber(num);
            orderItem.setProduct(product);
            orderItem.setUid(user.getId());
            ois.add(orderItem);
        }
        session.setAttribute("ois",ois);
        return "success";
    }
    @RequestMapping("/deleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(Integer oiid,HttpSession session){
        List<OrderItem> orderItems= (List<OrderItem>) session.getAttribute("ois");
        Iterator<OrderItem> iterator = orderItems.iterator();
        while(iterator.hasNext()){
            OrderItem orderItem=iterator.next();
            if(orderItem.getId().intValue()==oiid){
                iterator.remove();
            }
        }
        session.setAttribute("ois",orderItems);
        return "success";
    }
    @RequestMapping("/changeOrderItem")
    @ResponseBody
    public String changeOrderItem(Integer pid,Integer num,HttpSession session){
        List<OrderItem> orderItems= (List<OrderItem>) session.getAttribute("ois");
        Iterator<OrderItem> iterator = orderItems.iterator();
        while(iterator.hasNext()){
            OrderItem orderItem=iterator.next();
            if(orderItem.getPid().intValue()==pid){
                orderItem.setNumber(num);
            }
        }
        session.setAttribute("ois",orderItems);
        return "success";
    }
    @RequestMapping("/createOrder")
    public ModelAndView createOrder(Integer pid, Integer num, Boolean direct, HttpSession session, Order order){
        User user= (User) session.getAttribute("user");
        order.setUid(user.getId());
        List<OrderItem> orderItems=null;
        ModelAndView modelAndView = new ModelAndView();
        if(direct){
            orderItems=new ArrayList<>();
            OrderItem orderItem = new OrderItem();
            orderItem.setPid(pid);
            orderItem.setNumber(num);
            orderItems.add(orderItem);
            order=orderService.creatOrder(order,orderItems);
        }else{
            orderItems= (List<OrderItem>) session.getAttribute("ois");
            order=orderService.creatOrder(order,orderItems);
        }
        Float total=0F;
        for(OrderItem orderItem:orderItems){
            Product product = productsService.getProductByid(orderItem.getPid());
            total+=product.getPromotePrice()*orderItem.getNumber();
        }
        modelAndView.addObject("total",total);
        modelAndView.addObject("oid",order.getId());
        modelAndView.setViewName("fore/alipay");
        return modelAndView;
    }

    @RequestMapping("/payed")
    private ModelAndView payed(Integer oid){
        ModelAndView modelAndView = new ModelAndView();
        Order order=orderService.payOrder(oid);
        modelAndView.addObject("o",order);
        modelAndView.setViewName("fore/payed");
        return modelAndView;
    }
    @RequestMapping("/bought")
    public ModelAndView bought(HttpSession session){
        User user= (User) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        List<Order> orders=orderService.getOrdersByUId(user.getId());
        modelAndView.addObject("os",orders);
        modelAndView.setViewName("fore/bought");
        return modelAndView;
    }
    @RequestMapping("/confirmPay")
    public ModelAndView confirmPay(Integer oid){
        ModelAndView modelAndView = new ModelAndView();
        Order order=orderService.getOrderById(oid);
        modelAndView.addObject("o",order);
        modelAndView.setViewName("fore/confirmPay");
        return modelAndView;
    }
    @RequestMapping("/orderConfirmed")
    public String orderConfirmed(Integer oid){
        orderService.confim(oid);
        return "fore/orderConfirmed";
    }
    @RequestMapping("/review")
    public ModelAndView review(Integer oid){
        ModelAndView modelAndView = new ModelAndView();
        Order order = orderService.getOrderById(oid);
        Product product = order.getOrderItems().get(0).getProduct();
        modelAndView.addObject("o",order);
        modelAndView.addObject("p",product);
        modelAndView.setViewName("fore/review");
        return modelAndView;
    }
    @RequestMapping("/doreview")
    public String doreview(String content,Integer pid,Integer oid,HttpSession session){
        User user= (User) session.getAttribute("user");
        orderService.doReview(oid);
        productsService.doReview(pid,content,user.getId());
        return "redirect:bought";
    }
}
