package com.letv.mocker.mock.api;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.letv.mocker.framework.util.FilePropertiesManager;
import com.letv.mocker.ui.service.SystemService;

@Controller("api")
public class Test {
    private static Logger logger = Logger.getLogger(Test.class);

    @Autowired
    private SystemService sysService;

    @RequestMapping(method = RequestMethod.GET, value = "/mockapi/test")
    public String test(
            @RequestParam(value = "param", required = false) String param,
            Model model) {
        String returnPage = null;
        final String template = "{\"status\":%d,\"msg\":\"%s\"}";
        try {
            // test read param from pom.xml
            testReadFromPOM();
            returnPage = "common/json";
        } catch (final Exception e) {
            model.addAttribute("res", String.format(template, 0, "error"));
            returnPage = "common/error";
        }
        return returnPage;
    }

    public static void testReadFromPOM() {
        // java代码读取pom.xml的参数
        final Properties p = FilePropertiesManager
                .getProperties("/applicatioin.properties");
        final String name = p.getProperty("name");
        final String version = p.getProperty("version");
        final String artifactId = p.getProperty("artifactId");
        final String packaging = p.getProperty("packaging");
        logger.info(String
                .format("read project params from pom.xml: name = %s ; version = %s ; artifactId = %s ; packaging = %s",
                        name, version, artifactId, packaging));
    }
}