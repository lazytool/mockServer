package com.letv.mocker.framework.servlet;

import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.letv.mocker.framework.listener.SpringUtil;
import com.letv.mocker.framework.util.OsUtil;
import com.letv.mocker.ui.dao.ScenariosDao;
import com.letv.mocker.ui.dao.SysDao;

public class InitDBServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(InitDBServlet.class);
    private static String uuid = UUID.randomUUID().toString();

    /**
     * 初始化servlet
     */
    @Override
    public void init() throws ServletException {
        try {
            logger.info(uuid + "`============ InitDBServlet START ============");
            createDBTables();
            logger.info(uuid + "`============ InitDBServlet END ============");
        } catch (final Exception e) {
            logger.error(uuid + "`=========== InitDBServlet ERROR =========>"
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        System.out.println("InitDBServlet destory: update MODE = proxy");
        // 更新MODE
        final SysDao sysDao = (SysDao) SpringUtil.getBean("sysDao");
        sysDao.update("MODE", "proxy");
        System.out.println("InitDBServlet destroyed successful!");
    }

    private static void createDBTables() throws Exception {

        // 创建系统相关数据表system,并初始化参数和值
        final SysDao sysDao = (SysDao) SpringUtil.getBean("sysDao");
        if (sysDao.sysTableCount() == 0) {
            sysDao.createSysTable();
            sysDao.insert("MODE", "proxy");// 模式: proxy/record/mock
            sysDao.insert("MOCKPATH", OsUtil.getDefaultMockDataPath()); // 默认mock数据结果存放目录
            sysDao.insert("MOCK_HEADER_BLACK_LIST", "Content-Length"); // HttpResponse返回的HeaderMap中,不会被录制到mock.xml中的Header,多个以|分割
            sysDao.insert("LOCAL_REQS", "/mockapi/");// 不会被过滤器UrlFilter拦截的请求路径关键词(目前主要包含各种设置和页面api)
            // 当MODE是record模式时,只录制以下指定的请求(url中带有以下关键字,多个以|分割);若不限制置空
            sysDao.insert("RECORD_URLKEY", "");// RECORD_URLKEY=api.itv.letv.com|static.itv.letv.com|d.itv.letv.com
            // 限制客户端IP的黑白名单,这两个参数为互斥关系,不能同时起作用.同一个IP不能同时写在两个参数下
            sysDao.insert("CLIENT_WHITE_LIST", "");// 只允许以下客户端IP发出的请求访问该系统,多个以|分割;若ip都不限制则置空
            sysDao.insert("CLIENT_BLACK_LIST", "");// 黑名单不允许以下客户端IP发出的请求访问该系统,多个以|分割;若ip都不限制则置空

            logger.info(uuid + "`数据表system创建完成!");
        } else {
            logger.info(uuid + "`数据表system已存在,无需创建");
        }

        // 创建客户端IP-测试场景关联关系表
        final ScenariosDao scenariosDao = (ScenariosDao) SpringUtil
                .getBean("scenariosDao");
        if (scenariosDao.scenariosTableCount() == 0) {
            scenariosDao.createScenariosTable();
            logger.info(uuid + "`数据表scenarios创建完成!");
        } else {
            logger.info(uuid + "`数据表scenarios已存在,无需创建");
        }
    }
}
