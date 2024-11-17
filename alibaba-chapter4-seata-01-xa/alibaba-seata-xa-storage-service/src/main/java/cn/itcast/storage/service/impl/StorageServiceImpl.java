package cn.itcast.storage.service.impl;

import cn.itcast.storage.exception.ServiceException;
import cn.itcast.storage.mapper.StorageMapper;
import cn.itcast.storage.service.StorageService;
import io.seata.spring.annotation.GlobalLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 虎哥
 */
@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private StorageMapper storageMapper;

    @Transactional
    @Override
    public void deduct(String commodityCode, int count) {
        log.info("开始扣减库存");
        try {
            // TODO select for update
            storageMapper.deduct(commodityCode, count);
        } catch (Exception e) {
            throw new ServiceException("扣减库存失败，可能是库存不足！", "500");
        }
        log.info("扣减库存成功");
    }
}
