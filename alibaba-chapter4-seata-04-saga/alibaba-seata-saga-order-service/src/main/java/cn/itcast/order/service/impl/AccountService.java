package cn.itcast.order.service.impl;

import cn.itcast.order.client.AccountClient;
import cn.itcast.order.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("accountService")
public class AccountService {
    @Resource
    private AccountClient accountClient;

    public Boolean deduct(String userId, Integer money){
            ResponseEntity<Void> deduct = accountClient.deduct(userId, money);
            return deduct.getStatusCode().is2xxSuccessful();
    }

    public Boolean rollback(String userId, Integer money){
        ResponseEntity<Void> deduct = accountClient.rollback(userId, money);
        return deduct.getStatusCode().is2xxSuccessful();
    }
}
