package com.taobao.service;

import com.github.pagehelper.PageInfo;
import com.taobao.pojo.Category;
import com.taobao.pojo.Product;
import com.taobao.pojo.PropertyAndValue;

import java.util.List;

public interface ProductsService {
    List<Category> getCategoryList();

    List<Category> fillProducts(List<Category> categoryList);

    List<Category> fillByRowProducts(List<Category> categoryList);

    Category getCategoryById(Integer cid);

    PageInfo<Product> getProductPageByCid(Integer cid, Integer page, int i);

    Product getProductPageByid(Integer pid);

    Product fillSingleImg(Product product);

    List<PropertyAndValue> getPropertyByProduct(Product product);

    Product fillDetailImg(Product product);

    Product getProductByid(Integer pid);

    void doReview(Integer pid, String content,Integer uid);
}
