package com.accountquota.config;

import com.accountquota.bean.BaseResult;
import com.accountquota.enums.RetCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ValidatedExceptionHandler {

    /**
     * 处理@Validated参数校验失败异常
     *
     * @param exception 异常类
     * @return 响应
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResult exceptionHandler(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        String str ="";
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            for (ObjectError error : errors) {
                    FieldError fieldError = (FieldError) error;
                    str=fieldError.getDefaultMessage();
                    break;
            }
        }
        BaseResult baseRes = new BaseResult();
        baseRes.setMessage(str);
        baseRes.setRetCode(RetCodeEnum.PARAMETER_ERROR.getCode());
        return baseRes;
    }
}
