package com.accountquota.bean;

import com.accountquota.enums.RetCodeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResult<T> implements Serializable {

    /**
     * 返回错误码
     */
    public int retCode;

    /**
     * 返回信息
     */
    public String message;

    /**
     * 返回数据
     */
    public T data;

    public BaseResult() {
        this(RetCodeEnum.SUCCESS.getCode(), RetCodeEnum.SUCCESS.getDesc(), null);
    }

    public BaseResult(int retCode) {
        this(retCode, "", null);
    }

    public BaseResult(T data) {
        this(RetCodeEnum.SUCCESS.getCode(), RetCodeEnum.SUCCESS.getDesc(), data);
    }

    public BaseResult(int retCode, String msg) {
        this(retCode, msg, null);
    }

    public BaseResult(int retCode, String message, T data) {
        this.retCode = retCode;
        this.message = message;
        this.data = data;
    }

    public static <R> BaseResult<R> ok(R data) {
        BaseResult<R> baseResult = new BaseResult<>();
        baseResult.setData(data);
        return baseResult;
    }

    public static <R> BaseResult<R> error(String message) {
        BaseResult<R> baseResult = new BaseResult<>();
        baseResult.setRetCode(RetCodeEnum.ERROR.getCode());
        baseResult.setMessage(message);
        return baseResult;
    }

    public static BaseResult<?> ok() {
        return new BaseResult<>();
    }

    public static <R> BaseResult<R> error(Integer code, String message) {
        BaseResult<R> baseResult = new BaseResult<>();
        baseResult.setRetCode(code);
        baseResult.setMessage(message);
        return baseResult;
    }
}
