package com.letv.mocker.ui.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.letv.mocker.ui.dao.ScenariosDao;
import com.letv.mocker.ui.vo.Scenarios;

@Service("uiService")
public class UIServiceImpl implements UIService {

    @Resource
    private ScenariosDao scenariosDao;

    @Override
    public List<Scenarios> getAllScenarios() {

        return this.scenariosDao.findAll();
    }

    @Override
    public List<Scenarios> getScenarios(String name, String ip) {

        final List<Scenarios> list = this.scenariosDao.findScenarios(name, ip);
        return list;
    }
}
