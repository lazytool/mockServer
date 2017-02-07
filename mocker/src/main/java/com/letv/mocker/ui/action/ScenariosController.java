package com.letv.mocker.ui.action;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.letv.mocker.ui.service.UIService;
import com.letv.mocker.ui.vo.DatagridMsg;
import com.letv.mocker.ui.vo.Scenarios;

/**
 * 场景相关页面
 */
@Controller
public class ScenariosController {

    @Autowired
    private UIService uiService;

    // @Resource
    // private ScenariosDao scenariosDao;

    @RequestMapping(method = { RequestMethod.GET }, value = { "/" })
    public String homePage(Model model) {
        System.out.println("home page");
        return "home";
    }

    @RequestMapping(method = { RequestMethod.GET }, value = { "/man.html" })
    public String manPage(Model model) {
        System.out.println("home page");
        return "man";
    }

    /**
     *
     * */
    @RequestMapping("/scenarios/list")
    public String scriptlist(Model model) {
        return "scenarios/list";
    }

    /**
     * 场景列表 DataGrid
     * @param name
     *            搜索条件mock测试集合名称
     * @param ip
     *            搜索条件client ip列表
     */
    @RequestMapping(value = "/scenarios/search")
    public String showScriptList(Model model,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "ip", required = false) String ip,
            HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(name)) {
                name = null;
            }
            if (StringUtils.isBlank(ip)) {
                ip = null;
            }
            final List<Scenarios> scenariosList = this.uiService.getScenarios(
                    name, ip);

            final DatagridMsg obj = new DatagridMsg(scenariosList.size(),
                    scenariosList);
            final String json = new Gson().toJson(obj);

            response.getWriter().println(json);
            return null;
        } catch (final Exception e) {
            e.printStackTrace();
            model.addAttribute("res", e.getMessage());
            return "common/error";
        }
    }

    /**
     * 初始化新增页面
     */
    @RequestMapping(value = "/scenarios/addPage")
    public String addPage(Model model) {
        try {

            return "scenarios/add";
        } catch (final Exception e) {
            e.printStackTrace();
            model.addAttribute("res", e.getMessage());
            return "common/error";
        }
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/scenarios/add")
    public String add(Model model,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "ip", required = false) String ip) {
        try {
            model.addAttribute("res", "hello");
            return "scenarios/add";
        } catch (final Exception e) {
            e.printStackTrace();
            model.addAttribute("res", e.getMessage());
            return "common/error";
        }
    }

}
