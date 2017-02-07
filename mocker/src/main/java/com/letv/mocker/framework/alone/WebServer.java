package com.letv.mocker.framework.alone;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Example WebServer class which sets up an embedded Jetty appropriately
 * whether running in an IDE or in "production" mode in a shaded jar.
 */
public class WebServer {

    private Server server;
    private final int port;
    private final String bindInterface;
    // true表示在eclipse中直接执行main方法,使用PROJECT_RELATIVE_PATH_TO_WEBAPP路径加载webapp目录;false标识再生产环境中使用(打成jar包)
    private final boolean isRunningInIDE;

    // TODO: You should configure this appropriately for your environment
    public static final String LOG_PATH = "./logs/access/yyyy_mm_dd.request.log";
    // java -jar执行独立jar包时web.xml相对路径
    public static final String WEB_XML = "META-INF/webapp/WEB-INF/web.xml";
    // 在IDE中启动jetty时webapp的相对路径
    public static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/webapp";

    public WebServer(int aPort, boolean isRunningInIDE) {
        this(aPort, null, isRunningInIDE);
    }

    public WebServer(int aPort, String aBindInterface, boolean isRunningInIDE) {
        this.port = aPort;
        this.bindInterface = aBindInterface;
        this.isRunningInIDE = isRunningInIDE;
    }

    public void start() throws Exception {
        this.server = new Server();
        this.server.setThreadPool(this.createThreadPool());
        this.server.addConnector(this.createConnector());
        this.server.setHandler(this.createHandlers());
        this.server.setStopAtShutdown(true);

        this.server.start();
    }

    public void join() throws InterruptedException {
        this.server.join();
    }

    public void stop() throws Exception {
        this.server.stop();
    }

    /**
     * 根据相对于项目根目录的相对路径获取资源全路径
     **/
    public static URL getResource(String aResource) {
        return Thread.currentThread().getContextClassLoader()
                .getResource(aResource);
    }

    private ThreadPool createThreadPool() {
        // for your environment - this is an example only
        QueuedThreadPool _threadPool = new QueuedThreadPool();
        _threadPool.setMinThreads(10);
        _threadPool.setMaxThreads(100);
        return _threadPool;
    }

    private SelectChannelConnector createConnector() {
        SelectChannelConnector _connector = new SelectChannelConnector();
        _connector.setPort(this.port);
        _connector.setHost(this.bindInterface);
        return _connector;
    }

    private HandlerCollection createHandlers() {
        WebAppContext _ctx = new WebAppContext();
        _ctx.setContextPath("/");
        if (this.isRunningInIDE) {
            _ctx.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);
        } else {
            _ctx.setWar(getShadedWarUrl());
        }

        List<Handler> _handlers = new ArrayList<Handler>();

        _handlers.add(_ctx);

        HandlerList _contexts = new HandlerList();
        _contexts.setHandlers(_handlers.toArray(new Handler[0]));

        RequestLogHandler _log = new RequestLogHandler();
        _log.setRequestLog(this.createRequestLog());

        HandlerCollection _result = new HandlerCollection();
        _result.setHandlers(new Handler[] { _contexts, _log });

        return _result;
    }

    private RequestLog createRequestLog() {
        NCSARequestLog _log = new NCSARequestLog();

        File _logPath = new File(LOG_PATH);
        _logPath.getParentFile().mkdirs();

        _log.setFilename(_logPath.getPath());
        _log.setRetainDays(90);
        _log.setExtended(false);
        _log.setAppend(true);
        _log.setLogTimeZone("GMT");
        _log.setLogLatency(true);
        return _log;
    }

    /**
     * 此方法只适用于打成jar包后
     */
    public static String getShadedWarUrl() {
        String _urlStr = getResource(WEB_XML).toString();
        // Strip off "WEB-INF/web.xml"
        return _urlStr.substring(0, _urlStr.length() - 15);
    }
}
