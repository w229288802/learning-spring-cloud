package cn.itcast.account.service.impl;

import cn.itcast.account.entity.AccountFreeze;
import cn.itcast.account.mapper.AccountFreezeMapper;
import cn.itcast.account.mapper.AccountMapper;
import cn.itcast.account.service.AccountTCCService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountTCCServiceImpl implements AccountTCCService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountFreezeMapper freezeMapper;

    @Override
    @Transactional
    public void deduct(String userId, int money) {
        // 数据库的money是unsigned字段，不可能为负数，所以这里不用检测余额
        // 直接扣减为负数会抛出异常，这里的事务注解回滚
        // 0. 获取事务id
        String xid = RootContext.getXID();
        // 业务悬挂处理，防止已经发起回滚操作后，阻塞的try恢复，进行扣减
        // 导致无法confirm也无法cancel
        // 1. 判断freeze中是否有冻结记录，如果有，一定是CANCEL执行过，需要拒绝业务
        AccountFreeze oldFreeze = freezeMapper.selectById(xid);
        if (oldFreeze != null) {
            // 拒绝
            return;
        }
        // 1. 扣减可用余额
        accountMapper.deduct(userId, money);
        // 2. 记录冻结金额，事务状态
        AccountFreeze freeze = new AccountFreeze();
        freeze.setUserId(userId);
        freeze.setFreezeMoney(money);
        freeze.setState(AccountFreeze.State.TRY);
        freeze.setXid(xid);
        freezeMapper.insert(freeze);
    }

    @Override
    public boolean confirm(BusinessActionContext ctx) {
        // 因为try获取成功后进入confirm，意味着分支状态检查通过
        // 发起了事务提交指令，free表的数据就没有意义了，直接删除即可
        // 1. 获取事务id
        String xid = ctx.getXid();
        // 2. 根据id删除冻结记录
        int count = freezeMapper.deleteById(xid);
        return count == 1;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {
        String xid = ctx.getXid();
        // 0. 查询冻结记录，可以走数据库，也可以走上下文
        AccountFreeze freeze = freezeMapper.selectById(xid);
        String userId = ctx.getActionContext("userId").toString();
        // 1. 空回滚判断，判断freeze是否为null，为null证明try没执行，需要空回滚
        if (freeze == null) {
            // 证明try没执行，需要空回滚，记录一下这个回滚的信息
            freeze = new AccountFreeze();
            freeze.setUserId(userId);
            freeze.setFreezeMoney(0);
            freeze.setState(AccountFreeze.State.CANCEL);
            freeze.setXid(xid);
            freezeMapper.insert(freeze);
            return true;
        }
        // 2. 幂等判断，只要cancel执行了，这个状态一定是CANCEL
        // 所以判断这个值就可以知道是否幂等，防止上一轮cancel超时后重复执行cancel
        if (freeze.getState() == AccountFreeze.State.CANCEL) {
            // 已经处理过一次CANCEL了，无需重复处理
            return true;
        }
        // 1. 恢复可用余额
        accountMapper.refund(freeze.getUserId(), freeze.getFreezeMoney());
        // 2. 将冻结金额清零，状态改为CANCEL
        freeze.setFreezeMoney(0);
        freeze.setState(AccountFreeze.State.CANCEL);
        int count = freezeMapper.updateById(freeze);
        return count == 1;
    }
}
