package com.test;

import com.netflix.loadbalancer.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.omg.CORBA.SystemException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title:</p>
 * <p>Description:CommonRight  Controller </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: PCCW</p>
 *
 * @author Welge
 * @version 1.0
 * @date 2019/7/1
 */
@SpringBootApplication
@EnableZuulProxy
@EnableCircuitBreaker
public class ZuulApplication {

    @Bean
    public HttpClientBuilder HttpClientBuilder(){
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.addInterceptorFirst(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse httpResponse, HttpContext httpContext) throws SocketException {
                if (httpResponse.getStatusLine().getStatusCode() != 200) {
                    throw new SocketException("HTTP异常" + ":" + httpResponse.getStatusLine().getStatusCode());
                }
            }
        });
        return httpClientBuilder;
    }

    private static ConcurrentHashMap<Server, Boolean> isCircuitBreakerTrippedMap = new ConcurrentHashMap();

    @Bean
    public AvailabilityFilteringRule availabilityFilteringRule() throws IllegalAccessException {
        AvailabilityFilteringRule availabilityFilteringRule = new AvailabilityFilteringRule();
        CompositePredicate predicate = CompositePredicate.withPredicate(new AvailabilityPredicate(availabilityFilteringRule, null) {

            @Override
            public boolean apply(PredicateKey input) {
                boolean circuitBreakerTripped = getLBStats().getSingleServerStat(input.getServer()).isCircuitBreakerTripped();
                Boolean circuitBreakerTrippedOld = (Boolean) ObjectUtils.defaultIfNull(isCircuitBreakerTrippedMap.get(input.getServer()), false);
                if (circuitBreakerTripped ^ circuitBreakerTrippedOld) {
                    if (circuitBreakerTripped) {
                        System.out.println("系统管理员请处理，服务器 " + input.getServer().getMetaInfo().getInstanceId()  + " 发生断路");
                    }
                    isCircuitBreakerTrippedMap.put(input.getServer(), circuitBreakerTripped);
                }
                return super.apply(input);
            }
        })
                .addFallbackPredicate(AbstractServerPredicate.alwaysTrue())
                .build();
        FieldUtils.writeDeclaredField(availabilityFilteringRule, "predicate", predicate, true);
        return availabilityFilteringRule;
    }

    @Bean
    public PatternServiceRouteMapper serviceRouteMapper(){
        return new PatternServiceRouteMapper("(?<name>^.+)-(?<version>v.+$)", "${version}/${name}");
    }

    /*@Bean
    @ConditionalOnMissingBean
    public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
                                            ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
                                            IRule rule, IPing ping) {
        ZoneAwareLoadBalancer<Server> balancer = LoadBalancerBuilder.newBuilder()
                .withClientConfig(config).withRule(rule).withPing(ping)
                .withServerListFilter(serverListFilter).withDynamicServerList(serverList)
                .buildDynamicServerListLoadBalancer();
        return balancer;
    }*/

    /*public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulApplication.class).web(true).run(args);
    }*/
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class, args);
    }
}
