package com.letv.mocker.framework.listener;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Spring相关工具类
 */
public class SpringUtil {

    // 此参数会在系统启动时候被SpringContextAware初始化
    public static ApplicationContext context = null;

    private static ServletContext servletContext = null;

    /**
     * spring上下文提供两种注入方式：1启动servlet的时候从web.xml注入 2.若没有启动web服务或servlet注入失败,直接手动注入
     */
    public static ApplicationContext getDefaultWebApplicationContext() {
        if (context == null) {
            context = new FileSystemXmlApplicationContext(new String[] {
                    "file:src/main/webapp/WEB-INF/applicationContext.xml",
                    "file:src/main/webapp/WEB-INF/mocker-servlet.xml", });

        }
        return context;
    }

    /***
     * 获取Spring容器管理的Bean
     */
    public static Object getBean(String beanName) {
        Object obj = null;
        try {

            final ApplicationContext ctx = getDefaultWebApplicationContext();
            obj = ctx.getBean(beanName);
        } catch (final Exception e) {
            System.out.println("not exit Bean【{" + beanName
                    + "}】in WebApplicationContext");
            e.printStackTrace();
        }
        return obj;

    }

    public static ApplicationContext getWebApplicationContext() {
        return context;
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static void setServletContext(ServletContext servletContext) {
        SpringUtil.servletContext = servletContext;
    }

    public static void setAttribute(String key, Object value) {
        try {
            servletContext.setAttribute(key, value);
        } catch (final Exception e) {
            System.out.println("setAttribute to servletContext error!");
            e.printStackTrace();
        }
    }

    public static Object getAttribute(String key) {
        Object param = null;
        try {
            param = servletContext.getAttribute(key);
        } catch (final Exception e) {
            System.out.println("getAttribute from servletContext error!");
            e.printStackTrace();
        }
        return param;
    }
}