package cn.itcast.account.web;

import cn.itcast.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author 虎哥
 */
@RestController
@RequestMapping("account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PutMapping("/{userId}/{money}")
    public ResponseEntity<Void> deduct(@PathVariable("userId") String userId, @PathVariable("money") Integer money){
        accountService.deduct(userId, money);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/{money}")
    public ResponseEntity<Void> delete(@PathVariable("userId") String userId, @PathVariable("money") Integer money){
        accountService.rollback(userId, money);
        return ResponseEntity.noContent().build();
    }

}
