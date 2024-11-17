package cn.itcast.order.service.impl;

import cn.itcast.order.client.AccountClient;
import cn.itcast.order.client.StorageClient;
import cn.itcast.order.entity.Order;
import cn.itcast.order.exception.ServiceException;
import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.service.OrderService;
import feign.FeignException;
import io.seata.saga.engine.StateMachineEngine;
import io.seata.saga.statelang.domain.StateMachineInstance;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 虎哥
 */
@Slf4j
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    @Lazy //需要控制StateMachineConfiguration与FlywayAutoConfiguration的加载顺序，只果不懒加载，会默认先初始化StateMachineConfiguration
    private StateMachineEngine stateMachineEngine;

    private OrderMapper orderMapper;

    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }


    //默认超时时间60s
    @Override
    public Boolean saveOrder(String businessKey, Order order) {
        // 创建订单
        orderMapper.insert(order);
        return true;
    }

    @Override
    public Boolean deleteOrder(String businessKey, Order order) {
        orderMapper.deleteById(order.getId());
        return true;
    }

    //默认超时时间60s
    @Override
    public Order create(Order order) {
        try {

            //由于没有做空回滚，业务悬挂，幂等处理。当前流程
            // 1.订单创建 补偿：删除订单
            // 2.扣减账户 补偿：删除订单
            // 3.扣减库存 补偿：还原账户
            //正常流程，要根据订单号去做空回滚判断，业务悬挂判断
            // 1.订单创建 补偿：删除订单
            // 2.扣减账户 补偿：还原账户
            // 3.扣减库存 补偿：还原库存


            Map<String, Object> startParams = new HashMap<String, Object>(7);
            String businessKey = String.valueOf(System.currentTimeMillis());
            startParams.put("businessKey", businessKey);
            startParams.put("order", order);
            startParams.put("userId", order.getUserId());
            startParams.put("money", order.getMoney());
            startParams.put("commodityCode", order.getCommodityCode());
            startParams.put("count", order.getCount());

            StateMachineInstance instance = stateMachineEngine.startWithBusinessKey("order", null, businessKey, startParams);
            if(instance.getException()!=null) {
                throw instance.getException();
            }

        } catch (Exception e) {
            log.error("下单失败，原因:{}", e.getMessage(), e);
            throw new ServiceException(e.getMessage(), "500");
        }
        return order;
    }
}
