package com.letv.mocker.framework.exception;

/**
 * httpclient访问时抛出的异常
 * 1.Response Code 不是200 2.HttpClient请求服务器报IO异常
 */
public class CallHttpResultException extends Exception {

    private static final long serialVersionUID = -1793259807813016177L;

    public CallHttpResultException(String msg) {

        super(msg);
    }
}
