package com.letv.mocker.framework.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FilePropertiesManager {
    static Logger logger = Logger.getLogger(FilePropertiesManager.class);
    private static Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

    public static Map<String, Properties> getProperties() {
        return propertiesMap;
    }

    @SuppressWarnings("resource")
    public static Properties getProperties(String fileName) {
        InputStream is = null;
        Properties p = null;
        try {
            p = propertiesMap.get(fileName);
            if (p == null) {
                System.out.println("=====cache[" + fileName
                        + "] is null,Read from properties file!");
                try {
                    is = new FileInputStream(fileName);
                } catch (final Exception e) {
                    try {
                        if (fileName.startsWith("/")) {
                            is = FilePropertiesManager.class
                                    .getResourceAsStream(fileName);
                        } else {
                            is = FilePropertiesManager.class
                                    .getResourceAsStream("/" + fileName);
                        }
                    } catch (final Exception e2) {
                        logger.error("InputStream erro:" + e2.getMessage());
                        e.printStackTrace();
                    }
                }
                p = new Properties();
                p.load(is);
                propertiesMap.put(fileName, p);
                is.close();
            }
        } catch (final Exception e) {
            logger.error("read properties error!", e);
            e.printStackTrace();
        }
        return p;
    }

    public static String getPropertyValue(String fileName, String strKey) {
        final Properties p = getProperties(fileName);
        try {
            return p.getProperty(strKey);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存属性到文件
     */
    public static void storeProperties(Properties prop, String fileName) {
        try {
            final FileOutputStream oFile = new FileOutputStream(fileName, true);// true表示追加打开
            prop.store(oFile, "The New properties file");
            oFile.close();
        } catch (final Exception e) {
            logger.error("store properties error!", e);
            e.printStackTrace();
        }

    }

    private static Properties setProperties(String fileName, String key,
            String value) {
        final Properties prop = getProperties(fileName);
        prop.setProperty(key, value);
        return prop;
    }

    /**
     * 更新constants.properties文件
     */
    public static void updateConstants(String key, String value) {
        final Properties p = setProperties("/constants.properties", key, value);
        storeProperties(p, "/constants.properties");
    }

    public static void updateConstants(Map<String, String> propMap) {
        final Properties prop = getProperties("/constants.properties");
        final Set<String> keys = propMap.keySet();
        for (final String key : keys) {
            prop.setProperty(key, propMap.get(key));
        }
        storeProperties(prop, "/constants.properties");
    }

    /**
     * 清空properties缓存
     * @param fileName
     *            properties文件相对路径(eg: /constants.properties);若为null表示全部清空
     */
    private static boolean clean(String fileName, String uuid) {
        try {
            if (StringUtils.isBlank(fileName)) {
                propertiesMap.clear();
            } else {
                final Properties p = propertiesMap.get(fileName);
                if (p != null) {
                    propertiesMap.put(fileName, null);
                }
            }
            return true;
        } catch (final Exception e) {
            System.out.println(uuid
                    + "`===clean caches for properties error! key=" + fileName);

            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清理properties缓存并重新加载
     */
    public static boolean reloadProperties(String filePath, String uuid) {
        try {
            clean(filePath, uuid);
            getProperties(filePath);
            return true;
        } catch (final Exception e) {
            System.out
                    .println(uuid
                            + "`===reload caches for properties error! key="
                            + filePath);
            e.printStackTrace();
        }
        return false;
    }
}