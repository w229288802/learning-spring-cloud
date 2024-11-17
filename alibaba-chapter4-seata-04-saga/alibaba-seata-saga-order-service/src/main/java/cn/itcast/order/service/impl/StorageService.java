package cn.itcast.order.service.impl;

import cn.itcast.order.client.StorageClient;
import cn.itcast.order.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("storageService")
public class StorageService {
    @Resource
    private StorageClient client;

    public Boolean deduct(String code, Integer count){
        client.deduct(code, count);
        return true;
    }

    public Boolean rollback(String code, Integer count){
        client.rollback(code, count);
        return true;
    }
}
