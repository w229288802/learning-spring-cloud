package cn.itcast.order.service;

import cn.itcast.order.entity.Order;

public interface OrderService {

    /**
     * 订单保存
     */
    Boolean saveOrder(String businessKey, Order order);

    /**
     * 订单删除
     */
    Boolean deleteOrder(String businessKey, Order order);

    /**
     * 创建订单
     */
    Order create(Order order);
}