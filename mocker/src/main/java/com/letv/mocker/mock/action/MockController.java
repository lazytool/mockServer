package com.letv.mocker.mock.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.letv.mocker.framework.exception.CallHttpResultException;
import com.letv.mocker.framework.exception.ServiceException;
import com.letv.mocker.framework.filter.UrlFilter;
import com.letv.mocker.framework.util.Cache;
import com.letv.mocker.framework.util.Constants;
import com.letv.mocker.framework.util.HttpClientUtil;
import com.letv.mocker.framework.util.IOUtil;
import com.letv.mocker.framework.util.IPUtil;
import com.letv.mocker.framework.util.XmlUtil;
import com.letv.mocker.mock.http.Response;
import com.letv.mocker.mock.vo.Mock;
import com.letv.mocker.mock.vo.MockInterface;
import com.letv.mocker.mock.vo.MockResponse;

@Controller
public class MockController {
    private static final Logger logger = LoggerFactory
            .getLogger(MockController.class);

    public static final String URL_SPLIT_TAG = "~";
    private static final String CHARSET = "UTF-8";

    /**
     * 录制模式:录制请求,生成mock文件和响应数据文件;响应请求
     */
    @RequestMapping("/mockapi/record/**")
    public void record(Model model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            final String uuid = UUID.randomUUID().toString();

            final String clientIp = IPUtil.getClientIpAddr(request);
            final String realurl = request.getRequestURL().toString()
                    .replace("/mockapi/record", "");
            final String realHost = IPUtil.getHost(realurl);
            final String host_port = IPUtil.getHostAndPort(realurl);
            final String port = host_port.replace(realHost, "");
            final String type = request.getMethod();
            final String queryStr = this.getRequestParams(request);

            // doRequest
            final HttpResponse httpResponse = this.doRequest(request, realurl,
                    "record", uuid);
            final Response myResponse = new Response(response, httpResponse,
                    CHARSET);

            // record
            final String uriPath = request.getRequestURI().replace(
                    "/mockapi/record/", "");
            final boolean canRecord = this.checkRecord(realurl, uriPath);

            if (httpResponse != null && canRecord) {
                final MockInterface mockInterface = UrlFilter
                        .getMockInterface(String.format("http://%s/%s",
                                host_port, uriPath));

                if (mockInterface == null) {// 若不存在mock.xml,就直接录制
                    this.doRecord(null, uriPath, queryStr, realHost, port,
                            type, myResponse, false, uuid);

                } else { // 检查若有匹配的mock数据就不再录制;无匹配mock数据且全部mock条数不超过30条就录制生成一条mock数据;若无匹配的mock数据且超出30条，就覆盖default数据
                    final List<Mock> mocks = mockInterface.getMockResponse()
                            .getMockList();
                    if (!this.mockExist(mocks, queryStr)) {
                        if (mocks.size() < 30) {
                            this.doRecord(mockInterface, uriPath, queryStr,
                                    realHost, port, type, myResponse, false,
                                    uuid);
                        } else {
                            this.doRecord(mockInterface, uriPath, queryStr,
                                    realHost, port, type, myResponse, true,
                                    uuid);
                        }
                    } else {
                        logger.info(uuid + "|" + clientIp + "|record|" + type
                                + "|Response|ignore EXISTED request: "
                                + realurl);
                    }
                }
            }

            // 返回结果 proxy
            this.doProxy(request, realurl, myResponse, uuid);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 代理模式:只透传
     */
    @RequestMapping("/mockapi/proxy/**")
    public void proxy(Model model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String uuid = UUID.randomUUID().toString();
        final String url = request.getRequestURL().toString()
                .replace("/mockapi/proxy", "");

        final HttpResponse httpResponse = this.doRequest(request, url, "proxy",
                uuid);
        final Response myResponse = new Response(response, httpResponse,
                CHARSET);
        this.doProxy(request, url, myResponse, uuid);
    }

    /**
     * mock模式:有mock数据的返回mock数据,没有则采用default
     * mock节点的方式返回(默认直接透传,可配置proxy或返回Exceptioin)
     */
    @RequestMapping({ "/mockapi/mock/**" })
    public void mock(Model model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String uuid = UUID.randomUUID().toString();
        final String clientIp = IPUtil.getClientIpAddr(request);
        try {

            final String realurl = request.getRequestURL().toString()
                    .replace("/mockapi/mock", "");
            final String method = request.getMethod();
            final String queryStr = this.getRequestParams(request);

            final String url = queryStr == null ? realurl : realurl + "?"
                    + queryStr;
            logger.info(String.format("%s|%s|mock|%s|Request|%s", uuid,
                    clientIp, method, url));

            // 从mockCache中查找对应URL的mock数据
            final Map<String, MockInterface> cache = Cache.getMockInterCache();
            final MockInterface mockInterObj = cache.get(realurl);
            if (mockInterObj == null) {
                throw new ServiceException("没有找到mock数据文件：" + realurl);
            } else {
                final Mock mock = this.validateParam(request, queryStr,
                        mockInterObj);
                if (mock == null) {
                    throw new ServiceException("mock.xml中没找到符合查询条件的mock数据");
                } else {
                    // mock处理(需要区分是否为default数据)
                    this.doMock(request, mockInterObj, mock, response,
                            clientIp, method, uuid);
                }
            }
        } catch (final ServiceException mockEx) {
            throw ((Exception) mockEx.getClazz().newInstance());
        } catch (final Exception e) {
            logger.error(String.format("%s|%s|mock|ERROR|%s|%s", uuid,
                    clientIp, e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * 获取Http请求参数
     */
    private String getRequestParams(HttpServletRequest request) {
        String queryStr = null;
        // 所有的GET/POST请求参数都是以get请求方式保存到mock.xml中,拼接URL字符串作为查询缓存的条件
        if ("GET".equals(request.getMethod())) {
            queryStr = request.getQueryString();
        } else {
            final Map<String, String[]> params = request.getParameterMap();
            for (final String key : params.keySet()) {
                final String[] values = params.get(key);
                for (int i = 0; i < values.length; i++) {
                    final String value = values[i];
                    queryStr += key + "=" + value + "&";
                }
            }

        }
        return queryStr;
    }

    /**
     * 录制接口 若mock接口目录不存在,新录制生成mock目录,mock.xml和default.json;
     * 若mock接口数据目录存在,更新default数据或追加mock数据。 新增录制数据后 更新缓存
     */
    private void doRecord(MockInterface mockInterface, String urlPath,
            String queryStr, String realHost, String port, String type,
            Response myResponse, boolean isUpdateDefault, String uuid)
            throws Exception {

        String mockFilePath = null;
        List<Mock> mockList = null;
        MockResponse mockResponse = null;
        final String hostDir = StringUtils.isBlank(port) ? realHost : realHost
                + URL_SPLIT_TAG + port;
        // mock接口不存在,新录制生成mock目录,mock.xml和default.json
        boolean ok = false;
        if (mockInterface == null) {

            urlPath = urlPath.replaceAll("/", URL_SPLIT_TAG);
            String mockpath = Cache.getSysPropertiesCache().get("MOCKPATH");
            if (mockpath.endsWith(Constants.SPLIT)) {
                mockpath = mockpath.substring(0, mockpath.length() - 2);
            }
            mockFilePath = new StringBuilder().append(mockpath)
                    .append(Constants.SPLIT).append(myResponse.getProtocol())
                    .append(Constants.SPLIT).append(hostDir)
                    .append(Constants.SPLIT).append(urlPath).toString();

            mockList = new ArrayList<Mock>();
            mockResponse = new MockResponse(mockList);

            final Mock mockDefault = new Mock("default", null, null, null);
            final Mock mock = new Mock(queryStr, uuid + ".json", null,
                    myResponse);
            mockList.add(mockDefault);
            mockList.add(mock);

            mockInterface = new MockInterface(myResponse.getProtocol(),
                    realHost, port, type, mockResponse, urlPath);
            // write to File
            ok = XmlUtil.toXMLFile(mockInterface, mockFilePath, "mock.xml");
            if (ok) {
                IOUtil.write(myResponse.getRes(), myResponse.getCharset(),
                        mockFilePath, uuid + ".json");
            }

            // mock接口已存在,追加mock数据或把新录制的mock数据覆盖default
        } else {

            mockFilePath = mockInterface.getAbsPath();

            mockList = mockInterface.getMockResponse().getMockList();
            if (isUpdateDefault) {
                final Iterator<Mock> it = mockList.iterator();
                while (it.hasNext()) {
                    final Mock m = it.next();
                    if ("defaullt".equals(m.getParams())) {
                        it.remove();
                    }
                }
                final Mock mockDefault = new Mock("default", "default.json",
                        null, myResponse);
                mockList.add(mockDefault);
                ok = XmlUtil.toXMLFile(mockInterface, mockFilePath, "mock.xml");
                if (ok) {
                    IOUtil.write(myResponse.getRes(), myResponse.getCharset(),
                            mockFilePath, "default.json");
                }
            } else {
                mockList.add(new Mock(queryStr, uuid + ".json", null,
                        myResponse));
                ok = XmlUtil.toXMLFile(mockInterface, mockFilePath, "mock.xml");
                if (ok) {
                    IOUtil.write(myResponse.getRes(), myResponse.getCharset(),
                            mockFilePath, uuid + ".json");
                }
            }
        }
        // 更新缓存
        if (ok) {
            Cache.reloadMockData(uuid);
            Thread.sleep(1000);
        }
    }

    /**
     * 请求代理URL,响应后关闭流
     */
    private void doProxy(HttpServletRequest request, String url,
            Response myResponse, String uuid) throws Exception {

        final String clientIp = IPUtil.getClientIpAddr(request);
        final String method = request.getMethod();

        logger.debug(String.format("%s|%s|proxy|%s|Response|%s|%s", uuid,
                clientIp, method, myResponse.getStatusCode(),
                myResponse.getRes()));
        logger.info(String.format("%s|%s|proxy|%s|Response|%s", uuid, clientIp,
                method, myResponse.getStatusCode()));

        myResponse.write();
    }

    /**
     * 处理mock数据:
     * 1.非default数据 检查优先级：isMockException > proxy > statusCode > resFileName;
     * 检查逻辑：若mock节点中isMockException=true,直接返回resFileName定义的异常;
     * 若proxy节点有值,此条mock请求被转发到proxy服务器; 若有延时<delay>配置,响应延时配置的时间,单位毫秒;
     * 否则读取resFileName文件并响应,若resFileName也为空,则返回mock数据status=200,body为空.
     * 2.default数据 检查优先级同上,区别是若resFileName文件为空,直接透传请求.
     */
    private void doMock(HttpServletRequest request, MockInterface mockInterObj,
            Mock mock, HttpServletResponse response, String clientIp,
            String method, String uuid) throws Exception {
        try {
            // 延时处理
            final long delay = mock.getDelay();
            if (delay > 0) {
                logger.info(uuid + "|Delay " + delay + " ms...");
                Thread.sleep(delay);
            }

            // 直接返回mock数据中自定义的异常
            if (mock.isMockException()) {
                throw new ServiceException(
                        Class.forName(mock.getResFileName()), "mock Exception");
            }

            final String proxyHost = mock.getProxy();
            // mock.xml中定义了proxy地址,转发到代理服务器地址
            if (StringUtils.isNotBlank(proxyHost)) {
                String url = request.getRequestURL().toString();
                final String host = IPUtil.getHostAndPort(url);
                url = url.replace(host, proxyHost).replace("/mockapi/mock", "");

                final HttpResponse httpResponse = this.doRequest(request, url,
                        "proxy", uuid);

                this.doProxy(request, url, new Response(response, httpResponse,
                        CHARSET), uuid);
                // 若resFileName存在,返回文件内容;若不存在,defaultMock透传,非defaultMock返回空内容
            } else {
                final String resFileName = mock.getResFileName();
                if (resFileName == null && "default".equals(mock.getParams())) {
                    final String url = request.getRequestURL().toString()
                            .replace("/mockapi/mock", "");
                    this.doProxy(
                            request,
                            url,
                            new Response(response, this.doRequest(request, url,
                                    "proxy", uuid), CHARSET), uuid);
                } else {
                    mock.setResFilePath(mockInterObj.getAbsPath()
                            + Constants.SPLIT + resFileName);
                    final Response myResponse = new Response(response, mock);

                    logger.debug(String.format("%s|%s|mock|%s|Response|%s|%s",
                            uuid, clientIp, method, myResponse.getStatusCode(),
                            myResponse.getRes()));
                    logger.info(String.format("%s|%s|mock|%s|Response|%s",
                            uuid, clientIp, method, myResponse.getStatusCode()));

                    myResponse.write();
                }
            }

        } catch (final Exception e) {
            throw e;
        }
    }

    /**
     * 限制请求url是否录制： 1.直接请求host根目录的不录制;2.RECORD_URLKEY限制规则
     */
    private boolean checkRecord(String realurl, String uriPath) {
        boolean canRecord = false;
        // 若请求根目录,不做录制
        if (StringUtils.isBlank(uriPath)) {
            return canRecord;
        }
        // 若url中包含RECORD_URLKEY限制字段, 只录制包含RECORD_URLKEY的请求,否则无限制
        final String recordLimit = Cache.getSysPropertiesCache().get(
                "RECORD_URLKEY");
        if (StringUtils.isBlank(recordLimit)) {
            canRecord = true;
        } else {
            final String[] recordLimitList = recordLimit.split("\\|");
            for (final String limit : recordLimitList) {
                if (StringUtils.isNotBlank(limit) && realurl.contains(limit)) {
                    canRecord = true;
                    break;
                }
            }
        }
        return canRecord;
    }

    /**
     * 验证mock.xml并返回符合request条件的mock数据,若都没有匹配值就返回default mock数据
     */
    private Mock validateParam(HttpServletRequest request, String queryStr,
            MockInterface mockInter) {
        final Map<String, String[]> requestParamMap = request.getParameterMap();
        final List<Mock> mocks = mockInter.getMockResponse().getMockList();

        Mock mockTo = null;
        Mock defaultMock = null;

        final boolean isMockException = (StringUtils.isNotBlank(queryStr))
                && (queryStr.toLowerCase().contains("exception"));
        // 如果请求参数不为空且包含exception,就抛出mockException异常
        if (isMockException) {
            for (final Mock mock : mocks) {
                final String paramKey = mock.getParams();
                if (StringUtils.isNotBlank(paramKey)) {
                    if (queryStr.toLowerCase().contains(paramKey.toLowerCase())) {
                        mock.setMockException(true);
                        mockTo = mock;
                        break;
                    }
                }
                if ("default".equals(mock.getParams())) {
                    defaultMock = mock;
                }
            }
        } else {
            // 循环读取mock数据,按先后顺序对比mock数据参数和请求参数相符,若没有相符条件(请求参数包含mock参数或参数相同),则返回default数据
            for (final Mock mock : mocks) {

                if ("default".equals(mock.getParams())) {
                    defaultMock = mock;
                    continue;
                }

                boolean ok = true;
                final String[] xmlParams = mock.getParams().split("&");

                for (final String param : xmlParams) {
                    if (StringUtils.isNotBlank(param)) {
                        final String[] key_value = param.split("=");
                        if (key_value.length == 0) {
                            continue;
                        }
                        final String[] requestParam = requestParamMap
                                .get(key_value[0]);
                        String xmlParamVal = key_value.length == 2 ? key_value[1]
                                : "";

                        try {
                            xmlParamVal = URLDecoder.decode(xmlParamVal,
                                    CHARSET);
                        } catch (final UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if ((requestParam == null)
                                || (!requestParam[0].equals(xmlParamVal))) {
                            ok = false;
                            break;
                        }
                    }
                }

                if (ok) {
                    mockTo = mock;
                    break;
                }

            }
        }
        return mockTo == null ? defaultMock : mockTo;
    }

    private boolean mockExist(List<Mock> mockList, String queryStr) {
        boolean isExist = false;
        // 循环读取mock数据,按先后顺序对比mock数据参数和请求参数相符,若没有相符条件(请求参数包含mock参数或参数相同),则返回default数据
        outer: for (final Mock mock : mockList) {
            final String xmlParamStr = mock.getParams();
            // 如果本条mock数据参数为空或为default,直接跳过
            if (null == xmlParamStr || "default".equals(xmlParamStr)) {
                continue;
            }
            // 如果参数不为空,对比是否和请求参数相同
            final String[] xmlParams = xmlParamStr.split("&");
            for (final String xmlparam : xmlParams) {
                if (StringUtils.isNotBlank(xmlparam)
                        && !queryStr.contains(xmlparam)) {
                    continue outer;
                }
            }
            isExist = true;
            break;
        }
        return isExist;
    }

    /**
     * 拼接并发送代理的请求URL,只支持HTTP GET/POST请求
     * @param request
     * @param url
     * @param response
     * @param model
     *            proxy/record/mock
     * @return httpResponse
     */
    private HttpResponse doRequest(HttpServletRequest request, String url,
            String model, String uuid) throws CallHttpResultException {

        final String clientIp = IPUtil.getClientIpAddr(request);
        HttpResponse httpResponse = null;

        if ("GET".equals(request.getMethod())) {
            final String query = request.getQueryString();
            if (StringUtils.isNotBlank(query)) {
                url += ("?" + query);
            }

            logger.info(String.format("%s|%s|%s|GET|Request|%s", uuid,
                    clientIp, model, url));

            httpResponse = HttpClientUtil.getHttpResponse(new HttpGet(), url,
                    null, null, 10000);

        } else {
            final Map<String, String[]> params = request.getParameterMap();
            final Map<String, String> paramsMap = new HashMap<String, String>();
            String queryString = "";
            for (final String key : params.keySet()) {
                final String[] values = params.get(key);
                for (int i = 0; i < values.length; i++) {
                    final String value = values[i];
                    queryString += key + "=" + value + "&";
                    paramsMap.put(key, value);
                }
            }
            queryString = queryString.substring(0, queryString.length() - 1);// queryString去掉最后一个空格

            logger.info(String.format("%s|%s|%s|POST|Request|%s", uuid,
                    clientIp, model, url + "?" + queryString));

            httpResponse = HttpClientUtil.getHttpResponse(new HttpPost(), url,
                    paramsMap, CHARSET, 10000);
        }
        return httpResponse;
    }

}