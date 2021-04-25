package org.lyfy.beyond.common.advice;

import org.lyfy.beyond.common.exception.BusinessException;
import org.lyfy.beyond.common.util.EnvUtil;
import org.lyfy.beyond.common.vo.resp.RespVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.stream.Stream;

/**
 * @author : Yuan.Pan 2019/12/12 2:14 PM
 */
@ControllerAdvice
public class ExceptionAdvice {

    private static final String PARAMS_ERR_CODE = "9504";
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionAdvice.class);

    @InitBinder
    protected void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(Date.class, new MillisDateEditor());
        Stream.of(Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, BigInteger.class, BigDecimal.class)
                .forEach(numberClass -> webDataBinder.registerCustomEditor(numberClass, new StripNumberEditor(numberClass)));
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public RespVo handlerBusinessException(BindException ex) {
        LOG.error("[BUSINESS-ERR e-{}]:", ex);
        return new RespVo<>(PARAMS_ERR_CODE, ex.getFieldError().getDefaultMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public RespVo handlerBusinessException(MethodArgumentNotValidException ex) {
        LOG.error("[BUSINESS-ERR e-{}]:", ex);
        return new RespVo<>(PARAMS_ERR_CODE, ex.getBindingResult().getFieldError().getDefaultMessage(), null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public RespVo handlerBusinessException(ConstraintViolationException ex) {
        LOG.error("[BUSINESS-ERR e-{}]:", ex);
        return new RespVo<>(PARAMS_ERR_CODE, ex.getConstraintViolations().iterator().next().getMessage(), null);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public RespVo handlerBusinessException(BusinessException ex) {
        LOG.error("[BUSINESS-ERR e-{}]:", ex);
        return ex.toResponseVo();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public RespVo handlerThrowable(Throwable th) {
        LOG.error("error", th);
        if (!EnvUtil.isProdEnv()) {
            return new RespVo<>(RespVo.FAIL_CODE, th.getMessage(), null);
        }
        return RespVo.FAIL();
    }
}
