package com.taobao.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.taobao.pojo.Category;
import com.taobao.pojo.Product;
import com.taobao.pojo.PropertyAndValue;
import com.taobao.pojo.Review;
import com.taobao.service.ProductsService;
import com.taobao.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ProductsController {
    @Reference
    private ProductsService productsService;
    @Reference
    private ReviewService reviewService;
    @RequestMapping("/home")
    public ModelAndView home(HttpSession session){
        ModelAndView modelAndView = new ModelAndView();
        List<Category> categoryList = productsService.getCategoryList();
        session.setAttribute("cs",categoryList);
        categoryList=productsService.fillProducts(categoryList);
        categoryList=productsService.fillByRowProducts(categoryList);
        modelAndView.addObject("cs",categoryList);
        modelAndView.setViewName("fore/home");
        return modelAndView;
    }
    @RequestMapping("/category")
    public ModelAndView category(Integer cid,Integer page){
        if(page==null){
            page=1;
        }
        ModelAndView modelAndView = new ModelAndView();
        Category category=productsService.getCategoryById(cid);
        PageInfo<Product> productPageInfo=productsService.getProductPageByCid(cid,page,12);
        modelAndView.addObject("c",category);
        modelAndView.addObject("pageInfo",productPageInfo);
        modelAndView.setViewName("fore/category");
        return modelAndView;
    }
    @RequestMapping("/product")
    public ModelAndView product(Integer pid){
        ModelAndView modelAndView = new ModelAndView();
        List<Category> categoryList = productsService.getCategoryList();
        Product product=productsService.getProductPageByid(pid);
        product=productsService.fillSingleImg(product);
        product=productsService.fillDetailImg(product);
        List<PropertyAndValue> propertyAndValues=productsService.getPropertyByProduct(product);
        List<Review> reviews=reviewService.getReviewByPid(pid);
        modelAndView.addObject("reviews",reviews);
        modelAndView.addObject("psv",propertyAndValues);
        modelAndView.addObject("p",product);
        modelAndView.addObject("cs",categoryList);
        modelAndView.addObject("c",product.getCid());
        modelAndView.setViewName("fore/product");
        return modelAndView;
    }
}
