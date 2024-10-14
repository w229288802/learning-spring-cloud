package com.test.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
public class DemoController {

    @Resource
    private RestTemplate restTemplate;

    /**
     * 消费者去访问具体服务，这种写法可以实现
     * 配置文件和代码的分离
     */
    @Value("${service-url.nacos-user-service}")
    private String serverURL;


    @GetMapping(value = "consumer/nacos")
    public String getDiscovery(){
        System.err.println(serverURL);
        return restTemplate.getForObject(serverURL+"/mashibing",String.class);
    }
}