package cn.appnx.sentinel.rule;

import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class RuleNacosPublisher implements DynamicRulePublisher {

    @Resource
    protected ConfigService configService;

    public static Map<String, Future<String>> futureMap = new ConcurrentHashMap<>();

    @Override
    public boolean publish(String app, List<?> rules, String type) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return false;
        }
        
        String dataId = app + type;
        
        CompletableFuture<String> future = new CompletableFuture<>();
        futureMap.put(dataId, future);
        configService.addListener(dataId, NacosConfigUtil.GROUP_ID, new AbstractListener() {

            @Override
            public void receiveConfigInfo(String configInfo) {
                future.complete(configInfo);
            }
        });
        return configService.publishConfig(dataId,
                NacosConfigUtil.GROUP_ID, JSON.toJSONString(rules));
    }
}
