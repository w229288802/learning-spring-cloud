# 在sentinel-dashboard中集成nacos

## 1.在sentinel-dashboard添加坐标依赖
```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>alibaba-sentinel-dashboard-nacos-starter</artifactId>
    <version>1.8.8</version>
</dependency>
```

## 2.当前项目用maven-dependency-plugin打包
mvn clean package install

## 3.sentinel-dashboard打包
mvn clean package -Dmaven.test.skip=true

## nacos写错不会报错
java -Dnacos.serverAddr=127.0.0.1:8848 -Dnacos.namespace= -jar sentinel-dashboard.jar


## 通过Zip方式添加 starter
Bandizip以仅存储方式打开jar包，将starter包中的文件复制到sentinel-dashboard.jar中


## 源码
1. sentinel-datasource-nacos包中 NacosDataSource.initNacosListener方法把内置listener注册到ClientWorker中的CacheData.listeners
2. nacos-client包中ClientWorker$LongPollingRunnable.run()方法中通过nacos的api获取sentinel规则配置,写入CacheData,调用写入CacheData.checkListenerMd5
3. sentinel-datasource-nacos包中 CacheData 中md5发生变更调用 NacosDataSource 接收 sentinel规则配置




