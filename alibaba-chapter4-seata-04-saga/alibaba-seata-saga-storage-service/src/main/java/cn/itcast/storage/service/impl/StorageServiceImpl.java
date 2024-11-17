package cn.itcast.storage.service.impl;

import cn.itcast.storage.exception.ServiceException;
import cn.itcast.storage.mapper.StorageMapper;
import cn.itcast.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 虎哥
 */
@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private StorageMapper storageMapper;

    @Override
    public void deduct(String commodityCode, int count) {
        log.info("开始扣减库存");
        try {
            storageMapper.deduct(commodityCode, count);
        } catch (Exception e) {
            throw new ServiceException("扣减库存失败，可能是库存不足！", "500");
        }
        log.info("扣减库存成功");
    }


    @Override
    public void rollback(String commodityCode, int count) {
        log.info("开始回滚");
        try {
            storageMapper.rollback(commodityCode, count);
        } catch (Exception e) {
            throw new ServiceException("回滚失败", "500");
        }
        log.info("回滚成功");
    }
}
