package cn.itcast.storage.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServiceException extends RuntimeException{
    private String msg;
    private String code;
}
