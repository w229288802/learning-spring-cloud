package cn.itcast.order.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private String msg;
    private String code;
    public ServiceException(String msg, String code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }
}
