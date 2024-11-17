package cn.itcast.order.service;

import cn.itcast.order.entity.Order;

public interface OrderService {

    /**
     * 创建订单
     */
    Order create(Order order);
}