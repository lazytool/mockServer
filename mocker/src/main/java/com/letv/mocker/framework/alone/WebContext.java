package com.letv.mocker.framework.alone;

import java.util.Properties;

import com.letv.mocker.framework.util.FilePropertiesManager;
import com.letv.mocker.framework.util.OsUtil;
import com.letv.mocker.framework.util.OsUtil.Os;

public class WebContext {

    private static String jarDir;// jar包父目录

    private static String JAR_NAME;// "mocker-1.0-SNAPSHOT.jar"

    /**
     * 获取jar包父目录的绝对路径
     */
    public static String getJarDir() {
        try {
            if (jarDir == null) {
                jarDir = WebServer.getShadedWarUrl();
                if (Os.isLinux) {
                    // jar:file:/home/fj/git-workspace/qa/mocker/target/mocker-1.0-SNAPSHOT.jar!/META-INF/webapp/
                    jarDir = jarDir.replace("jar:file:", "");
                } else {
                    // jar:file:/D:/mytest/mocker/mocker-1.0-SNAPSHOT.jar!/META-INF/webapp/
                    jarDir = jarDir.replace("jar:file:/", "");
                    jarDir = jarDir.replace("/", OsUtil.FILE_SEPARATOR);
                }

                jarDir = jarDir.substring(0, jarDir.indexOf(getJarName()));
            }
            return jarDir;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取项目包名
     */
    public static String getJarName() {
        try {
            if (JAR_NAME == null) {
                final Properties p = FilePropertiesManager
                        .getProperties("/applicatioin.properties");
                final String artifactId = p.getProperty("artifactId");
                final String version = p.getProperty("version");
                final String packaging = p.getProperty("packaging");
                JAR_NAME = artifactId + "-" + version + "." + packaging;
            }
            System.out
                    .println("=======================================> JAR_NAME = "
                            + JAR_NAME);
            return JAR_NAME;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // public static void setJarDir(String jarDir) {
    // WebContext.jarDir = jarDir;
    // }

}