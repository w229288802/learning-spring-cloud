package cn.itcast.storage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 虎哥
 */
@MapperScan("cn.itcast.storage.mapper")
@SpringBootApplication
public class SagaStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(SagaStorageApplication.class, args);
    }
}
