package cn.itcast.account.exception;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 处理业务异常
     *
     */
    @ExceptionHandler(ServiceException.class)
    public String handleHxdsException(HttpServletResponse response, ServiceException e) {
        response.setStatus(500);
        log.error("业务异常：{}", e.getMsg());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", e.getCode());
        jsonObject.put("msg", e.getMsg());
        return jsonObject.toJSONString();
    }
}