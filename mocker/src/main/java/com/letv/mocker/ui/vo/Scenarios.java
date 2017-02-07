package com.letv.mocker.ui.vo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 测试场景实体类
 */
public class Scenarios implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;// 包含的测试集合名称(唯一)
    private String ips;// 客户端ip列表(以|分割)

    private String[] clientIpList;// 客户端ip列表
    private final String model = "mock";// 启动模式mock／proxy

    private String status;// 状态: running/stop/error
    private String last_modify_time;// 最后修改时间

    public Scenarios() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIps() {
        return this.ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public String[] getClientIpList() {
        return this.clientIpList;
    }

    public void setClientIpList(String[] clientIpList) {
        this.clientIpList = clientIpList;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLast_modify_time() {
        return this.last_modify_time;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public String getModel() {
        return this.model;
    }

    @Override
    public String toString() {
        return "Scenarios [name=" + this.name + ", ips=" + this.ips
                + ", clientIpList=" + Arrays.toString(this.clientIpList)
                + ", model=" + this.model + ", status=" + this.status
                + ", last_modify_time=" + this.last_modify_time + "]";
    }

}
