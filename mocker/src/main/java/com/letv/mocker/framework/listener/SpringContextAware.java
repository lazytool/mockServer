package com.letv.mocker.framework.listener;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 此类只要在spring配置文件注册,spring加载的时候会直接实例化并执行setApplicationContext方法
 */
public class SpringContextAware implements ApplicationContextAware {

    /**
     * 获取到spring上下文并赋值给SpringUtil类
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SpringUtil.context = applicationContext;
    }
}