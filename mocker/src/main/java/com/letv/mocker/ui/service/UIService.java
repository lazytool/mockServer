package com.letv.mocker.ui.service;

import java.util.List;

import com.letv.mocker.ui.vo.Scenarios;

public interface UIService {

    public List<Scenarios> getAllScenarios();

    public List<Scenarios> getScenarios(String name, String ips);
}
