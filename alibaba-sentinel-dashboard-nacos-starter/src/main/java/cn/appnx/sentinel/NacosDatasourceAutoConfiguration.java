package cn.appnx.sentinel;

import cn.appnx.sentinel.rule.*;
import cn.appnx.sentinel.spring.CustomBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import(NacosConfig.class)
public class NacosDatasourceAutoConfiguration {

    @Bean
    public static CustomBeanPostProcessor customBeanPostProcessor(){
        return new CustomBeanPostProcessor();
    }

    @Bean
    public DynamicRulePublisher dynamicRulePublisher(){
        return new RuleNacosPublisher();
    }
    @Bean
    public DynamicRuleProvider dynamicRuleProvider(){
        return new RuleNacosProvider();
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
