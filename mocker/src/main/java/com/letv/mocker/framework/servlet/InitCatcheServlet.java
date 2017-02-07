package com.letv.mocker.framework.servlet;

import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.letv.mocker.framework.util.Cache;

public class InitCatcheServlet extends HttpServlet {
    private static final long serialVersionUID = -7451368905212103705L;
    private static final Logger logger = Logger
            .getLogger(InitCatcheServlet.class);

    @Override
    public void init() throws ServletException {
        final String uuid = UUID.randomUUID().toString();
        logger.info(uuid + "|============ InitCatcheServlet START ============");
        try {
            Cache.getMockInterCache();

            logger.info(uuid
                    + "|============ InitCatcheServlet END ============");
        } catch (final Exception e) {
            logger.error(uuid
                    + "|============ InitCatcheServlet ERROR ============");
            e.printStackTrace();
        }
    }
}
