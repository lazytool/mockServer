package com.letv.mocker.mock.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.letv.mocker.framework.util.XStreamCDATA;
import com.letv.mocker.mock.http.Response;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * 一条请求对应的mock响应对象
 * 
 * */
public class Mock {

	@XStreamAlias("params")
	@XStreamCDATA
	private String params;// 匹配到的请求参数

	@XStreamAlias("proxy")
	private String proxy;// 代理host,若有值则优先把映射到此mock下的请求转发到proxy服务器

	@XStreamAlias("resFileName")
	private String resFileName;// 若proxy无值,则把映射到此mock下的请求返回resFileName文件内容

	@XStreamAlias("statusCode")
	private int statusCode;// 响应状态码

	@XStreamAlias("delay")
	private long delay;// 延时时间(单位毫秒 为空或无节点表示不延时)

	@XStreamImplicit(itemFieldName = "header")
	private List<Header> headers; // 响应头信息

	// 不做持久化的参数
	@XStreamOmitField
	private boolean isMockException;// 是否要mock一条Exception数据
	@XStreamOmitField
	private String resFilePath;// mock响应文件的路径

	public Mock() {
		super();
	}

	public Mock(String params, String resFileName, String proxy,
			Response myResponse) {
		this.params = params;
		this.resFileName = resFileName;
		this.proxy = proxy;

		// 填充mock Response
		if (myResponse != null) {
			List<Header> headers = myResponse.getHeaders();
			if (headers != null && headers.size() > 0) {
				// 注意必须每个mock对象有自己的new headerList,不能直接引用,否则xml持久化出问题!
				this.headers = new ArrayList<Header>();
				for (Header header : headers) {
					this.headers.add(new Header(header.getName(), header
							.getValue()));
				}
			}
			this.statusCode = myResponse.getStatusCode();
		}

	}

	public String getParams() {
		return this.params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public boolean isMockException() {
		return this.isMockException;
	}

	public void setMockException(boolean isMockException) {
		this.isMockException = isMockException;
	}

	public String getResFileName() {
		return this.resFileName;
	}

	public void setResFileName(String resFileName) {
		this.resFileName = resFileName;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getResFilePath() {
		return resFilePath;
	}

	public void setResFilePath(String resFilePath) {
		this.resFilePath = resFilePath;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	@Override
	public String toString() {
		return "Mock [params=" + params + ", proxy=" + proxy + ", resFileName="
				+ resFileName + ", statusCode=" + statusCode + ", delay="
				+ delay + ", headers=" + Arrays.toString(headers.toArray())
				+ ", isMockException=" + isMockException + ", resFilePath="
				+ resFilePath + "]";
	}
}
