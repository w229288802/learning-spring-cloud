package cn.itcast.storage.web;

import cn.itcast.storage.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author 虎哥
 */
@RestController
@RequestMapping("storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 扣减库存
     * @param code 商品编号
     * @param count 要扣减的数量
     * @return 无
     */
    @PutMapping("/{code}/{count}")
    public ResponseEntity<Void> deduct(@PathVariable("code") String code,@PathVariable("count") Integer count){
        storageService.deduct(code, count);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{code}/{count}")
    public ResponseEntity<Void> rollback(@PathVariable("code") String code,@PathVariable("count") Integer count){
        storageService.rollback(code, count);
        return ResponseEntity.noContent().build();
    }
}
