package com.letv.mocker.framework.exception;

public class ServiceException extends Exception
{
  private static final long serialVersionUID = -4821020897201387425L;
  private String code;
  private String message;
  private Class<?> clazz;

  public ServiceException(Class<?> clazz, String code, String message)
  {
    super(message);
    this.code = code;
    this.message = message;
    this.clazz = clazz;
  }

  public ServiceException(Class<?> clazz, String message) {
    this(clazz, "-1", message);
  }

  public ServiceException(String code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public ServiceException(String message) {
    this("-1", message);
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Class<?> getClazz() {
    return this.clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }
}

/* Location:           /Users/fengjing/Documents/mock/WEB-INF/classes/
 * Qualified Name:     com.letv.mock.exception.ServiceException
 * JD-Core Version:    0.6.2
 */