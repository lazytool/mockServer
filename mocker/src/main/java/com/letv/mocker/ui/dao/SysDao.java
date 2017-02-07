package com.letv.mocker.ui.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.letv.mocker.ui.vo.SystemContext;

/**
 * 保存系统相关配置信息
 */
@Component("sysDao")
public interface SysDao {
    /**
     * 查询system表是否存在
     */
    public int sysTableCount();

    /**
     * 创建系统配置表
     */
    public void createSysTable();

    /**
     * 插入一条sys记录
     */
    public void insert(@Param("name") String name, @Param("value") String value);

    /**
     * 查询所有的属性
     */
    public List<SystemContext> findAll();

    /**
     * 根据属性名称查询值
     */
    public List<String> findValByName(@Param("name") String name);

    /**
     * 根据name更新value
     */
    public int update(@Param("name") String name, @Param("value") String value);

}
