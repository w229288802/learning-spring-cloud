package cn.itcast.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author 虎哥
 */
@FeignClient("account-service")
public interface AccountClient {

    @PutMapping("/account/{userId}/{money}")
    ResponseEntity<Void> deduct(@PathVariable("userId") String userId, @PathVariable("money") Integer money);

    @DeleteMapping("/account/{userId}/{money}")
    ResponseEntity<Void> rollback(@PathVariable("userId") String userId, @PathVariable("money") Integer money);
}
