package com.letv.mocker.framework.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

/**
 * 上下文监听类
 */
public class ContextListener extends ContextLoaderListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO
		try {
			System.err.println(String.format(
					"Servlet Context Init--> logs_dir=%s/mocker_logs",
					System.getProperty("user.home")));
		} catch (Exception e) {
			System.err.println("Error In Creating Log4j Dir-->"
					+ e.getMessage());
			e.printStackTrace();
		}
	}

}
