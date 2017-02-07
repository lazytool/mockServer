package com.letv.mocker.mock.api;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.letv.mocker.framework.util.Cache;
import com.letv.mocker.framework.util.FilePropertiesManager;

@Controller
public class CacheManager {
    private static final Logger logger = LoggerFactory
            .getLogger(CacheManager.class);

    private static String template = "{\"status\":%d,\"msg\":\"%s\"}";

    /**
     * 清除mock缓存或配置缓存
     * @param key
     *            若key=mock,重新加载mock缓存文件;
     *            若key=filenamexxx或不传,不用重启项目就可重新加载指定配置文件或constants.
     *            properties文件(缺省值)
     * @param pwd
     *            必传 eg:http://10.200.91.32/mockapi/clean?key=mock&pwd=admin
     *            更新全部mock数据; http://10.200.91.32/mockapi/clean.do?pwd=admin
     *            不必重启项目更新全部配置文件
     */
    @RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, value = { "/mockapi/clean" })
    public String cleanPropertiesCache(Model model, HttpServletRequest request,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "pwd", required = true) String pwd) {
        String resStr = null;
        final String uuid = UUID.randomUUID().toString();
        logger.info(uuid + "`---start clean properties cache---> clientIp="
                + request.getRemoteAddr() + ";key=" + key);

        if ("admin".equals(pwd)) {
            boolean ok = false;
            Map<?, ?> obj = null;
            if (StringUtils.isBlank(key)) {
                ok = Cache.reloadSysProperties(uuid);
                obj = Cache.getSysPropertiesCache();
            } else if ("mock".equals(key)) {
                ok = Cache.reloadMockData(uuid);
                obj = Cache.getMockInterCache();
            } else {
                final String filePath = key.startsWith("/") ? key : "/" + key;
                ok = FilePropertiesManager.reloadProperties(filePath, uuid);
                obj = FilePropertiesManager.getProperties();
            }
            if (ok) {
                // toJson
                final Gson gson = new GsonBuilder().disableHtmlEscaping()
                        .create();
                resStr = gson.toJson(obj);

            } else {
                resStr = String
                        .format(template, new Object[] { Integer.valueOf(0),
                                Boolean.valueOf(ok) });
            }
        } else {
            resStr = String.format(template, new Object[] { Integer.valueOf(0),
                    "pwd error!" });
        }

        model.addAttribute("res", resStr);

        return "return/commonRes";
    }
}