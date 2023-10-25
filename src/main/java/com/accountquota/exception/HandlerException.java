package com.accountquota.exception;

import com.accountquota.bean.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class HandlerException {

    @ExceptionHandler
    public BaseResult<String> handleServiceException(ScException e) {
        log.info("**************ScException**************");
        log.info("【业务异常】：{}", e.getMessage(), e);
        return BaseResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler
    public BaseResult<String> handleRuntimeException(RuntimeException e) {
        log.info("**************RuntimeException**************");
        log.info("【运行异常】：{}", e.getMessage(), e);
        return BaseResult.error(e.getMessage());
    }

    @ExceptionHandler
    public BaseResult<String> handleException(Exception e) {
        log.info("【系统异常】：{}", e.getMessage(), e);
        return BaseResult.error("系统异常");
    }

}
