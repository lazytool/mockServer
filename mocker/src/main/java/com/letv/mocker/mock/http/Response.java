package com.letv.mocker.mock.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.letv.mocker.framework.util.Cache;
import com.letv.mocker.framework.util.IOUtil;
import com.letv.mocker.mock.vo.Mock;

public class Response {

    private final HttpServletResponse servletResponse;
    private HttpResponse httpResponse;
    private String protocol;
    private String res;
    private int statusCode;
    private String charset;
    private List<com.letv.mocker.mock.vo.Header> headers;

    /***
     * httpClient数据组装响应报文
     */
    public Response(HttpServletResponse servletResponse,
            HttpResponse httpResponse, String charset) throws IOException {
        this.servletResponse = servletResponse;
        this.httpResponse = httpResponse;
        this.charset = charset;

        final String MOCK_HEADER_BLACK_LIST = Cache.getSysPropertiesCache()
                .get("MOCK_HEADER_BLACK_LIST");
        // 组装headers
        if (httpResponse != null) {
            final Header[] headers = httpResponse.getAllHeaders();
            this.headers = new ArrayList<com.letv.mocker.mock.vo.Header>();
            for (final Header header : headers) {
                final String name = header.getName();
                final String val = header.getValue();
                // System.out.println("HttpResponse Header|" + name + ":" +
                // val);
                // 黑名单中的Header不写入servletResponse
                if (!MOCK_HEADER_BLACK_LIST.contains(name)) {
                    this.servletResponse.addHeader(name, val);
                    this.headers.add(new com.letv.mocker.mock.vo.Header(name,
                            val));
                }
            }
            this.protocol = httpResponse.getProtocolVersion().getProtocol()
                    .toLowerCase();
            this.statusCode = httpResponse.getStatusLine().getStatusCode();
            this.servletResponse.setStatus(this.statusCode);
            this.res = new String(EntityUtils.toByteArray(httpResponse
                    .getEntity()), charset);
        }
    }

    /**
     * mock数据组装响应报文
     */
    public Response(HttpServletResponse servletResponse, Mock mock)
            throws IOException {
        this.servletResponse = servletResponse;
        // 组装headers
        this.headers = mock.getHeaders();
        if (this.headers != null) {
            for (final com.letv.mocker.mock.vo.Header header : this.headers) {
                // System.out.println("MockResponse Header|" + header.getName()
                // + ":" + header.getValue());
                this.servletResponse.addHeader(header.getName(),
                        header.getValue());
            }
        }
        this.statusCode = mock.getStatusCode() == 0 ? servletResponse
                .getStatus() : mock.getStatusCode();
        this.servletResponse.setStatus(this.statusCode);
        if (StringUtils.isNotBlank(mock.getResFilePath())) {
            this.res = IOUtil.readLine2String(new FileInputStream(new File(mock
                    .getResFilePath())));
        }
    }

    public void write() {
        PrintWriter out = null;
        try {
            out = this.servletResponse.getWriter();
            out.println(this.res);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public HttpServletResponse getServletResponse() {
        return this.servletResponse;
    }

    public HttpResponse getHttpResponse() {
        return this.httpResponse;
    }

    public String getRes() {
        return this.res;
    }

    public String getCharset() {
        return this.charset;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public List<com.letv.mocker.mock.vo.Header> getHeaders() {
        return this.headers;
    }

}
