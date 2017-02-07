package com.letv.mocker.mock.vo;

import java.util.Map;

import com.letv.mocker.framework.util.Cache;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("mockInterface")
public class MockInterface {

    public MockInterface() {
        super();
    }

    public MockInterface(String protocol, String host, String port,
            String type, MockResponse mockResponse, String absPath) {
        super();
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.type = type;
        this.mockResponse = mockResponse;
        this.absPath = absPath;
    }

    public static MockInterface getMockInterface(String uriPath) {
        uriPath = uriPath.startsWith("/") ? uriPath.substring(uriPath
                .indexOf("/") + 1) : uriPath;
        uriPath = uriPath.replaceAll("/", "-");
        final Map<String, MockInterface> mockInterCatche = Cache
                .getMockInterCache();
        final MockInterface mockInterface = mockInterCatche.get(uriPath);
        return mockInterface;
    }

    // 不做持久化
    @XStreamOmitField
    private String protocol;// 协议
    @XStreamOmitField
    private String host;
    @XStreamOmitField
    private String port;
    @XStreamOmitField
    private String absPath;// 存放该请求urlmock资源文件的绝对路径(该字段不做持久化)

    // 持久化参数
    @XStreamAlias("type")
    private String type;// 请求类型 GET POST
    @XStreamAlias("mockResponse")
    private MockResponse mockResponse;

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAbsPath() {
        return this.absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public MockResponse getMockResponse() {
        return this.mockResponse;
    }

    public void setMockResponse(MockResponse mockResponse) {
        this.mockResponse = mockResponse;
    }

}

/*
 * Location: /Users/fengjing/Documents/mock/WEB-INF/classes/ Qualified Name:
 * com.letv.mock.vo.MockInterface JD-Core Version: 0.6.2
 */