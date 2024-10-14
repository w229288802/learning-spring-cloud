package cn.appnx.sentinel.client;

import cn.appnx.sentinel.rule.DynamicRuleProvider;
import cn.appnx.sentinel.rule.DynamicRulePublisher;
import cn.appnx.sentinel.rule.NacosConfigUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class SentinelApiClientFactoryBean implements FactoryBean<Object> {

    private final DynamicRuleProvider dynamicRuleProvider;

    private final DynamicRulePublisher dynamicRulePublisher;

    public SentinelApiClientFactoryBean(DynamicRuleProvider dynamicRuleProvider, DynamicRulePublisher dynamicRulePublisher) {
        this.dynamicRuleProvider = dynamicRuleProvider;
        this.dynamicRulePublisher = dynamicRulePublisher;
    }

    private final Map<String, String> methodPublisherMap = new HashMap<String, String>(){{
        put("setFlowRuleOfMachineAsync", NacosConfigUtil.FLOW_DATA_ID_POSTFIX);
        put("setDegradeRuleOfMachine", NacosConfigUtil.DEGRADE_DATA_ID_POSTFIX);
        put("setSystemRuleOfMachine", NacosConfigUtil.SYSTEM_DATA_ID_POSTFIX);
        put("setAuthorityRuleOfMachine", NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX);
        put("setParamFlowRuleOfMachine", NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX);

        put("modifyApis", NacosConfigUtil.GATEWAY_API_FLOW_DATA_ID_POSTFIX);
        put("modifyGatewayFlowRules", NacosConfigUtil.GATEWAY_FLOW_DATA_ID_POSTFIX);
    }};

    private final Map<String, String> methodProviderMap = new HashMap<String, String>(){{
        put("fetchFlowRuleOfMachine", NacosConfigUtil.FLOW_DATA_ID_POSTFIX);
        put("fetchDegradeRuleOfMachine", NacosConfigUtil.DEGRADE_DATA_ID_POSTFIX);
        put("fetchSystemRuleOfMachine", NacosConfigUtil.SYSTEM_DATA_ID_POSTFIX);
        put("fetchAuthorityRulesOfMachine", NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX);
        put("fetchParamFlowRulesOfMachine", NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX);

        put("fetchApis", NacosConfigUtil.GATEWAY_API_FLOW_DATA_ID_POSTFIX);
        put("fetchGatewayFlowRules", NacosConfigUtil.GATEWAY_FLOW_DATA_ID_POSTFIX);
    }};

    @Override
    public Object getObject() throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Class.forName("com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient"));
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

                String postfix = methodPublisherMap.get(method.getName());

                if(StringUtils.isNotBlank(postfix)){
                    Object rules = Arrays.stream(args).filter(s -> s.getClass().isAssignableFrom(ArrayList.class)).findAny().get();
                    if(Future.class.isAssignableFrom(method.getReturnType())){
                        String finalPostfix = postfix;
                        return CompletableFuture.runAsync(()->{
                            try {
                                dynamicRulePublisher.publish(((String) args[0]), (List<?>) rules, finalPostfix);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    return dynamicRulePublisher.publish(((String) args[0]), (List<?>) rules, postfix);
                }

                postfix = methodProviderMap.get(method.getName());

                if(StringUtils.isNotBlank(postfix)){
                    Type actualTypeArgument = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                    if(ParameterizedType.class.isAssignableFrom(actualTypeArgument.getClass())){
                        actualTypeArgument = ((ParameterizedType) actualTypeArgument).getActualTypeArguments()[0];
                    }
                    List<?> rules = dynamicRuleProvider.getRules(((String) args[0]), ((Class<?>) actualTypeArgument), postfix);
                    if(Future.class.isAssignableFrom(method.getReturnType())){
                        return CompletableFuture.completedFuture(rules);
                    }
                    return rules;
                }

                // 添加代理逻辑
                return proxy.invokeSuper(obj, args);
            }
        });
        return enhancer.create();
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return Class.forName("com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
