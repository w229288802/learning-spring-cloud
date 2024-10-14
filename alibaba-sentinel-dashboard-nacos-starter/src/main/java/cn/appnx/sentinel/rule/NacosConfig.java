/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.appnx.sentinel.rule;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@Configuration
public class NacosConfig {

    @Value("${nacos.namespace:}")
    private String namespace;
    @Value("${nacos.serverAddr:localhost:8848}")
    private String serverAddr;


    @Bean
    public ConfigService nacosConfigService() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.NAMESPACE,namespace);
        properties.setProperty(PropertyKeyConst.SERVER_ADDR,serverAddr);

        NamingService nameService = NamingFactory.createNamingService(properties);

        if(!"UP".equals(nameService.getServerStatus())) {
            throw new IllegalStateException(String.format("Nacos server is not available:[%s]", serverAddr));
        }

        return ConfigFactory.createConfigService(properties);
    }
}
