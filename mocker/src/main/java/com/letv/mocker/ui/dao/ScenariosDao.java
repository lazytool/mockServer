package com.letv.mocker.ui.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.letv.mocker.ui.vo.Scenarios;

/**
 * 场景表
 */
@Component("scenariosDao")
public interface ScenariosDao {

    /**
     * 查询scenarios表是否存在
     */
    public int scenariosTableCount();

    /**
     * 创建场景表
     */
    public void createScenariosTable();

    /**
     * 查询所有场景
     */
    public List<Scenarios> findAll();

    /**
     * 根据ip查询场景
     */
    public List<Scenarios> findByIp(@Param("ip") String ip);

    /**
     * 根据自定义的条件查询
     */
    public List<Scenarios> findScenarios(
            @Param("mockCollectionName") String mockCollectionName,
            @Param("ip") String ip);

    /**
     * 插入一条记录
     */
    public void insert(@Param("name") String name, @Param("ips") String ips);

    /**
     * 根据name更新ips
     */
    public int update(@Param("name") String name, @Param("ips") String ips);

}
