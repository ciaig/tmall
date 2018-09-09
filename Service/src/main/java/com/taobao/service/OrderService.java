package com.taobao.service;

import com.taobao.pojo.Order;
import com.taobao.pojo.OrderItem;

import java.util.List;

public interface OrderService {
    Order creatOrder(Order order, List<OrderItem> orderItems);

    Order payOrder(Integer oid);

    List<Order> getOrdersByUId(Integer id);

    Order getOrderById(Integer oid);

    void confim(Integer oid);

    void doReview(Integer oid);
}
