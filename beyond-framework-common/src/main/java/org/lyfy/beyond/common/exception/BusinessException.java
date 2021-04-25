package org.lyfy.beyond.common.exception;

import org.lyfy.beyond.common.constant.BaseEnum;
import org.lyfy.beyond.common.vo.resp.RespVo;

/**
 * @author : Yuan.Pan 2019/12/12 2:09 PM
 */
public class BusinessException extends RuntimeException {

    private String code;

    private String message;

    public BusinessException(BaseEnum responseEnum) {
        super(responseEnum.getCode() + ":" + responseEnum.getMessage());
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }

    public BusinessException(BaseEnum responseEnum, Throwable throwable) {
        super(responseEnum.getCode() + ":" + responseEnum.getMessage(), throwable);
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.message = message;
    }

    public BusinessException(BaseEnum responseEnum, Object... bundleMsg) {
        this.code = responseEnum.getCode();
        this.message = (bundleMsg != null && bundleMsg.length > 0) ? String.format(responseEnum.getMessage(), bundleMsg) : responseEnum.getMessage();
    }

    public RespVo toResponseVo() {
        return new RespVo<>(code, message, null);
    }

}
