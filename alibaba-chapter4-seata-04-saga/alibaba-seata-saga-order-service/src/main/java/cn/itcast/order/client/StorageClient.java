package cn.itcast.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author 虎哥
 */
@FeignClient(value = "storage-service")
public interface StorageClient {
    @PutMapping("/storage/{code}/{count}")
    void deduct(@PathVariable("code") String code, @PathVariable("count") Integer count);

    @DeleteMapping("/storage/{code}/{count}")
    void rollback(@PathVariable("code") String code, @PathVariable("count") Integer count);
}
