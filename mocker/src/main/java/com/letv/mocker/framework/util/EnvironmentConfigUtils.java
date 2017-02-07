//package com.letv.mocker.framework.util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//
//import com.letv.mocker.mock.action.MockController;
//import com.letv.mocker.mock.vo.MockInterface;
//
//public class EnvironmentConfigUtils {
//    static Logger logger = Logger.getLogger(EnvironmentConfigUtils.class);
//    // static EnvironmentConfigUtils ec;
//    private static Map<String, Properties> propertiesMap = new HashMap<String, Properties>();
//
//    private static Map<String, MockInterface> mockFileCache = new HashMap<String, MockInterface>();
//
//    // public static EnvironmentConfigUtils getInstance() {
//    // if (ec == null) {
//    // ec = new EnvironmentConfigUtils();
//    // }
//    // return ec;
//    // }
//
//    public static Map<String, Properties> getProperties() {
//        return propertiesMap;
//    }
//
//    @SuppressWarnings("resource")
//    public static Properties getProperties(String fileName) {
//        InputStream is = null;
//        Properties p = null;
//        try {
//            p = propertiesMap.get(fileName);
//            if (p == null) {
//                System.out.println("=====cache[" + fileName
//                        + "] is null,Read from properties file!");
//                try {
//                    is = new FileInputStream(fileName);
//                } catch (final Exception e) {
//                    try {
//                        if (fileName.startsWith("/")) {
//                            is = EnvironmentConfigUtils.class
//                                    .getResourceAsStream(fileName);
//                        } else {
//                            is = EnvironmentConfigUtils.class
//                                    .getResourceAsStream("/" + fileName);
//                        }
//                    } catch (final Exception e2) {
//                        logger.error("InputStream erro:" + e2.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//                p = new Properties();
//                p.load(is);
//                propertiesMap.put(fileName, p);
//                is.close();
//            }
//        } catch (final Exception e) {
//            logger.error("read properties error!", e);
//            e.printStackTrace();
//        }
//        return p;
//    }
//
//    public static String getPropertyValue(String fileName, String strKey) {
//        final Properties p = getProperties(fileName);
//        try {
//            return p.getProperty(strKey);
//        } catch (final Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 保存属性到文件
//     */
//    public static void storeProperties(Properties prop, String fileName) {
//        try {
//            final FileOutputStream oFile = new FileOutputStream(fileName, true);// true表示追加打开
//            prop.store(oFile, "The New properties file");
//            oFile.close();
//        } catch (final Exception e) {
//            logger.error("store properties error!", e);
//            e.printStackTrace();
//        }
//
//    }
//
//    public static Properties setProperties(String fileName, String key,
//            String value) {
//        final Properties prop = getProperties(fileName);
//        prop.setProperty(key, value);
//        return prop;
//    }
//
//    /**
//     * 更新constants.properties文件
//     */
//    public static void updateConstants(String key, String value) {
//        final Properties p = setProperties("/constants.properties", key, value);
//        storeProperties(p, "/constants.properties");
//    }
//
//    public static void updateConstants(Map<String, String> propMap) {
//        final Properties prop = getProperties("/constants.properties");
//        final Set<String> keys = propMap.keySet();
//        for (final String key : keys) {
//            prop.setProperty(key, propMap.get(key));
//        }
//        storeProperties(prop, "/constants.properties");
//    }
//
//    public static Map<String, MockInterface> getMockInterCache() {
//        try {
//            if (mockFileCache == null) {
//                mockFileCache = new HashMap<String, MockInterface>();
//            }
//
//            if (mockFileCache.size() <= 0) {
//                System.out.println("mockFileCatche 为空,开始初始化mock接口数据...");
//                final String rootPath = getPropertyValue(
//                        "/constants.properties", "MOCKPATH");
//
//                final File[] protocolDirs = IOUtil.getFile(rootPath, null)
//                        .listFiles();
//                for (final File protocolDir : protocolDirs) {// 协议目录
//                    final File[] hostDirs = IOUtil.getFile(
//                            protocolDir.getAbsolutePath(), null).listFiles();
//                    for (final File hostDir : hostDirs) {// host目录
//                        final File[] reqPathDirs = IOUtil.getFile(
//                                hostDir.getAbsolutePath(), null).listFiles();
//                        for (final File reqPathDir : reqPathDirs) {// Request
//                                                                   // Path 目录
//                            try {
//                                final String absPath = reqPathDir
//                                        .getAbsolutePath();
//                                final String key = protocolDir.getName()
//                                        + "://"
//                                        + hostDir.getName().replace(
//                                                MockController.URL_SPLIT_TAG,
//                                                ":")
//                                        + "/"
//                                        + reqPathDir.getName().replaceAll(
//                                                MockController.URL_SPLIT_TAG,
//                                                "/");
//                                final MockInterface mockInter = XmlUtil
//                                        .toBeanFromFile(absPath, "mock.xml",
//                                                MockInterface.class);
//
//                                mockInter.setAbsPath(absPath);
//
//                                mockFileCache.put(key, mockInter);
//                            } catch (final Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                }
//                System.out.println("mockFileCatche 初始化mock接口数据完成!");
//            }
//        } catch (final Exception e) {
//            e.printStackTrace();
//        }
//        return mockFileCache;
//    }
//
//    public static boolean reloadMockData(String uuid) {
//        try {
//            mockFileCache.clear();
//            getMockInterCache();
//            return true;
//        } catch (final Exception e) {
//            System.out.println(uuid
//                    + "`===clean and reload mapCache for mockFile error!");
//
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 清空properties缓存
//     * @param fileName
//     *            properties文件相对路径(eg: /constants.properties);若为null表示全部清空
//     */
//    private static boolean clean(String fileName, String uuid) {
//        try {
//            if (StringUtils.isBlank(fileName)) {
//                propertiesMap.clear();
//            } else {
//                final Properties p = propertiesMap.get(fileName);
//                if (p != null) {
//                    propertiesMap.put(fileName, null);
//                }
//            }
//            return true;
//        } catch (final Exception e) {
//            System.out.println(uuid
//                    + "`===clean caches for properties error! key=" + fileName);
//
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 清理properties缓存并重新加载
//     */
//    public static boolean reloadProperties(String filePath, String uuid) {
//        try {
//            clean(filePath, uuid);
//            getProperties(filePath);
//            return true;
//        } catch (final Exception e) {
//            System.out
//                    .println(uuid
//                            + "`===reload caches for properties error! key="
//                            + filePath);
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 重新加载constants.properties文件
//     */
//    public static boolean reloadConstantsProperties(String uuid) {
//        final String filePath = "/constants.properties";
//        return reloadProperties(filePath, uuid);
//    }
// }