package com.letv.mocker.framework.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.letv.mocker.framework.util.Cache;
import com.letv.mocker.framework.util.IPUtil;
import com.letv.mocker.mock.vo.MockInterface;

/**
 * 请求URL拦截器
 */
public class UrlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final String uriStr = request.getRequestURI();
        final String realurl = request.getRequestURL().toString();
        final String clientIP = IPUtil.getClientIpAddr(request);
        final String localIP = IPUtil.getLocalIPAddr();

        // 检查黑白名单判断客户端IP是否允许访问mocker服务
        if (!accessValidate(clientIP)) {
            System.out.println("client [" + clientIP + "] 被禁止访问!");
            response.getWriter()
                    .write(" ["
                            + clientIP
                            + "] does not have remote access permission! Contact the server administrator.");
            // 不被代理过滤器拦截的请求(mocker服务本身接口和LOCAL_REQS指定配置接口)
        } else if (realurl.contains("127.0.0.1")
                || realurl.contains("localhost")
                || (StringUtils.isNotBlank(localIP) && realurl
                        .contains(localIP)) || reqNotInfilter(uriStr)) {

            chain.doFilter(request, response);

            // 对代理的请求拦截转发处理
        } else {

            String path = null;
            if (StringUtils.isBlank(uriStr)) {
                path = "/mockapi/%s";
            } else if (uriStr.startsWith("/")) {
                path = "/mockapi/%s" + uriStr;
            } else {
                path = "mockapi/%s/" + uriStr;
            }
            final String mode = Cache.getSysPropertiesCache().get("MODE");

            if ("mock".equals(mode)) {// mock模式:请求客户端host被允许mock且存在mock数据就返回mock数据,否则走代理模式
                final boolean ok = mockValidate(realurl);
                if (ok) {
                    path = String.format(path, mode);
                } else {
                    path = String.format(path, "proxy");
                }
            } else {// 录制模式或代理模式
                path = String.format(path, mode);

            }
            request.getRequestDispatcher(path).forward(req, response);
        }

    }

    /**
     * mock校验:读取mock缓存检查urlPath的mock数据文件mock.xml是否已经存在(check mock Directory)
     * @return true mock接口目录已经存在;false 本请求接口mock目录不存在
     */
    public static boolean mockValidate(String realPath) {
        return getMockInterface(realPath) == null ? false : true;
    }

    /**
     * 检查黑白名单,判断请求客户端的IP是否允许访问系统 ;本地请求都可访问,不做检查
     * @param clientHost
     *            客户端host
     * @return 允许访问返回true;不允许访问返回false
     */
    public static boolean accessValidate(String clientHost) {
        if ("localhost".equals(clientHost) || "127.0.0.1".equals(clientHost)) {
            return true;
        }

        final String white_hosts = Cache.getSysPropertiesCache().get(
                "CLIENT_WHITE_LIST");
        final String black_hosts = Cache.getSysPropertiesCache().get(
                "CLIENT_BLACK_LIST");
        if (StringUtils.isNotBlank(white_hosts)
                && !white_hosts.contains(clientHost)) {
            return false;
        }
        if (StringUtils.isNotBlank(black_hosts)
                && black_hosts.contains(clientHost)) {
            return false;
        }
        return true;
    }

    /**
     * 获取mock.xml描述的接口数据对象
     * @param realPath
     */
    public static MockInterface getMockInterface(String realPath) {

        final Map<String, MockInterface> mockInterCatche = Cache
                .getMockInterCache();
        return mockInterCatche.get(realPath);
    }

    /**
     * 判断uri是否为不会被过滤器UrlFilter拦截的请求
     * @param uri
     * @return flag true uri不被过滤器拦截 false被拦截
     */
    private static boolean reqNotInfilter(String uri) {
        boolean flag = false;

        final String reqs = Cache.getSysPropertiesCache().get("LOCAL_REQS");
        final String[] reqArry = reqs.split("\\|");
        for (final String reqStr : reqArry) {
            if (uri.contains(reqStr)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    // changed by fj:
    // 数据库相关操作挪到InitDBServlet的destory方法中。
    // tip:DB操作不要写在filter的init中,因为web.xml加载顺序是context-filter-listener-servlet,DB的初始化写在了servlet中init初始化时DB还没被初始化。
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}