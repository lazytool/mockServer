package com.letv.mocker.ui.service;

import java.util.List;

import com.letv.mocker.ui.vo.SystemContext;

public interface SystemService {

    public String getVal(String name) throws Exception;

    public List<SystemContext> getAll() throws Exception;

    /**
     * 根据name更新value
     */
    public void update(String name, String value) throws Exception;
    /**
     * 批量更新
     */
    // public void update(List<SystemContext> list) throws Exception;
}
