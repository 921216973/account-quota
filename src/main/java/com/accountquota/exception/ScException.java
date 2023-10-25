package com.accountquota.exception;

import com.accountquota.enums.RetCodeEnum;
import lombok.Data;

@Data
public class ScException extends RuntimeException {
    private int code = RetCodeEnum.ERROR.getCode();

    private String errorMessage = "";

    public ScException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public ScException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

    public ScException(int code, String message) {
        super(message);
        this.code = code;
        this.errorMessage = message;
    }

    public ScException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.errorMessage = message;
    }


    public ScException(Throwable cause) {
        super(cause);
    }
}
