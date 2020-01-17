package com.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ComputeController {
    @Autowired
    private DiscoveryClient client;
    @RequestMapping(value = "/add" ,method = RequestMethod.GET)
    public Integer add(@RequestParam Integer a, @RequestParam Integer b) {
        ServiceInstance instance = client.getInstances("compute-service").get(0);
        Integer r = a + b;
        log.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
        return r;
    }

    @RequestMapping(value = "/test" ,method = RequestMethod.GET)
    public ResponseEntity test() {
        System.out.println("11");
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}