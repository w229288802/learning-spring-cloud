package cn.itcast.account.service.impl;

import cn.itcast.account.exception.ServiceException;
import cn.itcast.account.mapper.AccountMapper;
import cn.itcast.account.service.AccountService;
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
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    @Transactional
    //脏写 curl -X PUT 'http://192.168.137.1:8083/account/user202103032042012/200'
    @GlobalLock(lockRetryInternal = 1000, lockRetryTimes = 30) //@GlobalLock + select for update 防止脏写
    public void deduct(String userId, int money) {
        log.info("开始扣款");
        try {
            // 只有select for update 才会进行重试，超出限制抛出LockConflictException
            accountMapper.selectForUpdate(userId);
            // TODO select for update
            accountMapper.deduct(userId, money);
        } catch (Exception e) {
            throw new ServiceException("扣款失败，可能是余额不足！", "500");
        }
        log.info("扣款成功");
    }
}
