package com.letv.mocker.ui.action;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.letv.mocker.framework.util.Cache;
import com.letv.mocker.ui.service.SystemService;
import com.letv.mocker.ui.vo.SystemContext;

/**
 * 系统配置相关页面
 */
@Controller
public class SystemController {

    @Autowired
    SystemService sysService;

    @RequestMapping(value = "/system/setting")
    public String showSettingPg(Model model) {
        try {
            final List<SystemContext> sysList = this.sysService.getAll();
            model.addAttribute("sysList", sysList);
            return "system/setting";
        } catch (final Exception e) {
            e.printStackTrace();
            model.addAttribute("res", e.getMessage());
            return "common/error";
        }
    }

    /**
     * 更新
     */
    @RequestMapping(value = "/system/update")
    public String setting(
            Model model,
            @RequestParam(value = "name", required = true) List<String> nameList,
            @RequestParam(value = "value", required = true) List<String> valueList,
            @RequestParam(value = "MODE", required = true) String modevalue) {

        try {
            final String uuid = UUID.randomUUID().toString();
            // 更新数据库
            this.sysService.update("MODE", modevalue);
            for (int i = 0; i < nameList.size(); i++) {
                final String value = valueList.get(i);
                this.sysService.update(nameList.get(i), value);
            }
            // 更新sysProperties缓存
            Cache.reloadSysProperties(uuid);
            // 更新mockdata缓存
            Cache.reloadMockData(uuid);
            model.addAttribute("res", "更新成功!");
            return "common/success";
        } catch (final Exception e) {
            e.printStackTrace();
            model.addAttribute("res", e.getMessage());
            return "common/error";
        }

    }
}
