package com.letv.mocker.framework.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.letv.mocker.framework.listener.SpringUtil;
import com.letv.mocker.mock.action.MockController;
import com.letv.mocker.mock.vo.MockInterface;
import com.letv.mocker.ui.dao.SysDao;
import com.letv.mocker.ui.vo.SystemContext;

public class Cache {

    static Logger logger = Logger.getLogger(Cache.class);

    private static Map<String, String> sysProperties = new HashMap<String, String>();// 系统变量

    private static Map<String, MockInterface> mockFileCache = new HashMap<String, MockInterface>();

    /**
     * sysProperties若为空就读取数据库重新加载,若不为空就直接返回
     */
    public static Map<String, String> getSysPropertiesCache() {
        if (sysProperties == null) {
            sysProperties = new HashMap<String, String>();
        }

        if (sysProperties.size() <= 0) {
            System.out.println("sysProperties 为空,从数据库查询并更新系统变量缓存...");

            // TODO 从数据库查询properties并赋值给sysProperties缓存
            final SysDao sysDao = (SysDao) SpringUtil.getBean("sysDao");
            final List<SystemContext> list = sysDao.findAll();
            for (final SystemContext sysPro : list) {
                sysProperties.put(sysPro.getName(), sysPro.getValue());
            }
        }
        return sysProperties;
    }

    /**
     * mockFileCache若为空就从文件读取mock数据缓存到mockFileCache中,若不为空就直接返回
     */
    public static Map<String, MockInterface> getMockInterCache() {
        try {
            if (mockFileCache == null) {
                mockFileCache = new HashMap<String, MockInterface>();
            }

            // mockFileCache被清空或者mock_data目录为空的时候
            if (mockFileCache.size() <= 0) {
                System.out.println("mockFileCatche 为空,开始初始化mock接口数据缓存...");

                // 获取mock数据的跟目录
                final String rootPath = getSysPropertiesCache().get("MOCKPATH");

                final File[] protocolDirs = IOUtil.getFile(rootPath, null)
                        .listFiles();
                for (final File protocolDir : protocolDirs) {// 协议目录
                    final File[] hostDirs = IOUtil.getFile(
                            protocolDir.getAbsolutePath(), null).listFiles();
                    for (final File hostDir : hostDirs) {// host目录
                        final File[] reqPathDirs = IOUtil.getFile(
                                hostDir.getAbsolutePath(), null).listFiles();
                        for (final File reqPathDir : reqPathDirs) {// Request
                                                                   // Path 目录
                            try {
                                final String absPath = reqPathDir
                                        .getAbsolutePath();
                                final String key = protocolDir.getName()
                                        + "://"
                                        + hostDir.getName().replace(
                                                MockController.URL_SPLIT_TAG,
                                                ":")
                                        + "/"
                                        + reqPathDir.getName().replaceAll(
                                                MockController.URL_SPLIT_TAG,
                                                "/");
                                final MockInterface mockInter = XmlUtil
                                        .toBeanFromFile(absPath, "mock.xml",
                                                MockInterface.class);

                                mockInter.setAbsPath(absPath);

                                mockFileCache.put(key, mockInter);
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                System.out.println("mockFileCatche 初始化mock接口数据完成!");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return mockFileCache;
    }

    /**
     * 清理mockFileCache缓存重新加载
     */
    public static boolean reloadMockData(String uuid) {
        try {
            mockFileCache.clear();
            getMockInterCache();
            return true;
        } catch (final Exception e) {
            System.out.println(uuid + " | reload mockFileCache error!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清理sysProperties缓存并重新加载
     */
    public static boolean reloadSysProperties(String uuid) {
        try {
            sysProperties.clear();
            getSysPropertiesCache();
            return true;
        } catch (final Exception e) {
            System.out.println(uuid + " | reload sysProperties error!");
            e.printStackTrace();
            return false;
        }

    }
}