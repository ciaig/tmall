package com.taobao.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taobao.dao.*;
import com.taobao.pojo.*;
import com.taobao.service.ProductsService;
import com.taobao.utils.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductsServiceImpl implements ProductsService {
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductImageMapper productImageMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private PropertyMapper propertyMapper;
    @Autowired
    private PropertyValueMapper propertyValueMapper;
    @Override
    public List<Category> getCategoryList() {
        List<Category> categoryList=null;
        Jedis jedis = jedisPool.getResource();
        if(jedis.exists("cs".getBytes())){
            byte[] bytes = jedis.get("cs".getBytes());
            categoryList =  SerializeUtil.unserializeForList(bytes);
            jedis.close();
            return categoryList;
        }
        categoryList = categoryMapper.selectByExample(null);
        byte[] bytes = SerializeUtil.serialize(categoryList);
        jedis.set("cs".getBytes(),bytes);
        jedis.close();
        return categoryList;
    }

    @Override
    public List<Category> fillProducts(List<Category> categoryList) {
        ProductExample productExample = new ProductExample();
        for(Category category:categoryList){
            productExample.clear();
            productExample.createCriteria().andCidEqualTo(category.getId());
            List<Product> productList = productMapper.selectByExample(productExample);
            for(Product product:productList){
                fillFirstImg(product);
            }
            category.setProducts(productList);
        }
        return categoryList;
    }

    @Override
    public List<Category> fillByRowProducts(List<Category> categoryList) {
        int productNumberEachRow = 8;
        for (Category c : categoryList) {
            List<Product> products =  c.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            c.setProductsByRow(productsByRow);
        }
        return categoryList;
    }

    @Override
    public Category getCategoryById(Integer cid) {
        Category category = categoryMapper.selectByPrimaryKey(cid);
        return category;
    }

    @Override
    public PageInfo<Product> getProductPageByCid(Integer cid, Integer page, int i) {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andCidEqualTo(cid);
        PageHelper.startPage(page,i);
        List<Product> products = productMapper.selectByExample(productExample);
        for(Product p:products){
            getSaleAndReview(p);
        }
        for(Product product:products){
            fillFirstImg(product);
        }
        PageInfo<Product> productPageInfo = new PageInfo<>(products);
        return productPageInfo;
    }

    @Override
    public Product getProductPageByid(Integer pid) {
        Product product = productMapper.selectByPrimaryKey(pid);
        fillFirstImg(product);
        getSaleAndReview(product);
        return product;
    }

    @Override
    public List<PropertyAndValue> getPropertyByProduct(Product product) {
        ArrayList<PropertyAndValue> propertyAndValues = new ArrayList<>();
        PropertyExample propertyExample = new PropertyExample();
        propertyExample.createCriteria().andCidEqualTo(product.getCid());
        PropertyValueExample propertyValueExample = new PropertyValueExample();
        List<Property> properties = propertyMapper.selectByExample(propertyExample);
        for(Property property:properties){
            propertyValueExample.clear();
            PropertyAndValue propertyAndValue = new PropertyAndValue();
            propertyAndValue.setProperty(property);
            propertyValueExample.createCriteria().andPtidEqualTo(property.getId());
            List<PropertyValue> propertyValues = propertyValueMapper.selectByExample(propertyValueExample);
            if(propertyValues.size()!=0){
                propertyAndValue.setPropertyValue(propertyValues.get(0));
            }else{
                PropertyValue propertyValue = new PropertyValue();
                propertyValue.setValue("");
                propertyAndValue.setPropertyValue(propertyValue);
            }
            propertyAndValues.add(propertyAndValue);
        }
        return propertyAndValues;
    }

    @Override
    public Product fillDetailImg(Product product) {
        ProductImageExample productImageExample = new ProductImageExample();
        ProductImageExample.Criteria criteria = productImageExample.createCriteria();
        criteria.andPidEqualTo(product.getId());
        criteria.andTypeEqualTo("type_detail");
        List<ProductImage> productImages = productImageMapper.selectByExample(productImageExample);
        product.setProductDetailImages(productImages);
        return product;
    }

    @Override
    public Product getProductByid(Integer pid) {
        Product product = productMapper.selectByPrimaryKey(pid);
        fillFirstImg(product);
        return product;
    }

    @Override
    @Transactional
    public void doReview(Integer pid, String content, Integer uid) {
        Review review = new Review();
        review.setUid(uid);
        review.setPid(pid);
        review.setContent(content);
        reviewMapper.insert(review);
    }

    @Override
    public Product fillSingleImg(Product product) {
        ProductImageExample productImageExample = new ProductImageExample();
        ProductImageExample.Criteria criteria = productImageExample.createCriteria();
        criteria.andPidEqualTo(product.getId());
        criteria.andTypeEqualTo("type_single");
        List<ProductImage> productImages = productImageMapper.selectByExample(productImageExample);
        product.setProductSingleImages(productImages);
        return product;
    }

    private void getSaleAndReview(Product p) {
        int saleNum=orderItemMapper.getCountByPid(p.getId());
        int reviewNum=reviewMapper.getCountByPid(p.getId());
        p.setSaleCount(saleNum);
        p.setReviewCount(reviewNum);
    }

    private void fillFirstImg(Product product) {
        ProductImageExample productImageExample = new ProductImageExample();
        productImageExample.createCriteria().andPidEqualTo(product.getId());
        product.setFirstProductImage(productImageMapper.selectByExample(productImageExample).get(0));
    }
}
