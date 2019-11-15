package com.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

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
public class ZuulApplication {

    /*@Bean
    public PatternServiceRouteMapper serviceRouteMapper(){
        return new PatternServiceRouteMapper("(?<name>^.+)-(?<version>v.+$)", "${version}/${name}");
    }*/

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
