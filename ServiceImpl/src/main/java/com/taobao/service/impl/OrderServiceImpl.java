package com.taobao.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.taobao.dao.OrderItemMapper;
import com.taobao.dao.OrderMapper;
import com.taobao.dao.ProductImageMapper;
import com.taobao.dao.ProductMapper;
import com.taobao.pojo.*;
import com.taobao.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductImageMapper productImageMapper;
    @Override
    @Transactional
    public Order creatOrder(Order order, List<OrderItem> orderItems) {
        order.setCreateDate(new Date());
        order.setOrderCode(UUID.randomUUID().toString());
        order.setStatus("waitPay");
        orderMapper.insert(order);
        for(OrderItem orderItem:orderItems){
            orderItem.setUid(order.getUid());
            orderItem.setOid(order.getId());
            orderItem.setId(null);
            orderItemMapper.insert(orderItem);
        }
        return order;
    }

    @Override
    @Transactional
    public Order payOrder(Integer oid) {
        Order order = orderMapper.selectByPrimaryKey(oid);
        order.setStatus("waitDelivery");
        orderMapper.updateByPrimaryKeySelective(order);
        order.setPayDate(new Date());
        return order;
    }

    @Override
    public List<Order> getOrdersByUId(Integer id) {
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andUidEqualTo(id);
        List<Order> orders = orderMapper.selectByExample(orderExample);
        for(Order order:orders){
            fillOrderItem(order);
        }
        return orders;
    }


    @Override
    public Order getOrderById(Integer oid) {
        Order order = orderMapper.selectByPrimaryKey(oid);
        fillOrderItem(order);
        return order;
    }

    @Override
    @Transactional
    public void confim(Integer oid) {
        Order order = orderMapper.selectByPrimaryKey(oid);
        order.setStatus("waitReview");
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    @Transactional
    public void doReview(Integer oid) {
        Order order = orderMapper.selectByPrimaryKey(oid);
        order.setStatus("finish");
        orderMapper.updateByPrimaryKeySelective(order);
    }

    private void fillFirstImg(Product product) {
        ProductImageExample productImageExample = new ProductImageExample();
        productImageExample.createCriteria().andPidEqualTo(product.getId());
        product.setFirstProductImage(productImageMapper.selectByExample(productImageExample).get(0));
    }
    private void fillOrderItem(Order order) {
        OrderItemExample orderItemExample = new OrderItemExample();
        Float total=0f;
        orderItemExample.clear();
        orderItemExample.createCriteria().andOidEqualTo(order.getId());
        List<OrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
        for(OrderItem orderItem:orderItems){
            Product product = productMapper.selectByPrimaryKey(orderItem.getPid());
            fillFirstImg(product);
            orderItem.setProduct(product);
            total+=product.getPromotePrice()*orderItem.getNumber();
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
    }
}
