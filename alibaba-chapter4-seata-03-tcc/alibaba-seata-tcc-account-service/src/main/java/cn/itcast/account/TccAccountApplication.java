package cn.itcast.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 虎哥
 */
@MapperScan("cn.itcast.account.mapper")
@SpringBootApplication
public class TccAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(TccAccountApplication.class, args);
    }
}
