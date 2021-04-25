package org.lyfy.beyond.common.vo.resp;

@Deprecated
public class ResponseVo<T> {

    public static final ResponseVo FAIL = new ResponseVo<>("0911", "系统正忙，请稍后重试", null);
    public static final ResponseVo SUCCESS = new ResponseVo<>("0000", "操作成功", null);
    public static final ResponseVo REQUEST_PARAM_ERROR = new ResponseVo<>("0002", "注意：${}", null);
    public static final ResponseVo NOT_LOGIN = new ResponseVo<>("0003", "请先登录", null);
    public static final ResponseVo TOO_FREQUENCY = new ResponseVo<>("0004", "您的操作太快了", null);

    private String resCode;
    private String resMsg;
    private T result;

    public ResponseVo() {
    }

    public ResponseVo(String resCode, String resMsg, T result) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.result = result;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
