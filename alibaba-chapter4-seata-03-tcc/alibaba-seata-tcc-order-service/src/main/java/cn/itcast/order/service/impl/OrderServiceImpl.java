package cn.itcast.order.service.impl;

import cn.itcast.order.client.AccountClient;
import cn.itcast.order.client.StorageClient;
import cn.itcast.order.entity.Order;
import cn.itcast.order.exception.ServiceException;
import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.service.OrderService;
import feign.FeignException;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 虎哥
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final AccountClient accountClient;
    private final StorageClient storageClient;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(AccountClient accountClient, StorageClient storageClient, OrderMapper orderMapper) {
        this.accountClient = accountClient;
        this.storageClient = storageClient;
        this.orderMapper = orderMapper;
    }

    //默认超时时间60s
    @Override
    @GlobalTransactional(timeoutMills = 60000)
    public Order create(Order order) {
        // 创建订单
        orderMapper.insert(order);
        try {
            // 扣用户余额
            accountClient.deduct(order.getUserId(), order.getMoney());
            //client.tm.defaultGlobalTransactionTimeout 默认 60 秒，TM 检测到分支事务超时或 TC 检测到 TM 未做二阶段上报超时后，发起对分支事务的回滚
            // 扣库存
            storageClient.deduct(order.getCommodityCode(), order.getCount());

        } catch (FeignException e) {
            log.error("下单失败，原因:{}", e.contentUTF8(), e);
            throw new ServiceException(e.contentUTF8(), "500");
        }
        return order;
    }
}
