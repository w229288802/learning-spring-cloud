package cn.appnx.sentinel.rule;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RuleNacosProvider  implements DynamicRuleProvider {

    private final Logger logger = LoggerFactory.getLogger(RuleNacosProvider.class);

    @Resource
    protected ConfigService configService;

    @Override
    public List<?> getRules(String appName, Class<?> clazz, String type) throws Exception {

        String dataId = appName + type;
        String rules = null;
        Future<String> future = RuleNacosPublisher.futureMap.get(dataId);
        if(future!=null) {
            try {
                rules = future.get(3000, TimeUnit.SECONDS);
            }  catch (Exception e) {
                logger.error("getRules error", e);
            }
        }
        if (StringUtil.isEmpty(rules)){
            rules = configService.getConfig(dataId,
                    NacosConfigUtil.GROUP_ID, 3000);
        }

        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(rules, clazz);
    }

}
