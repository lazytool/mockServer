package com.letv.mocker.framework.alone;

public class Main {

    // 打成jar包独立执行的时候该参数改成false; 在eclipse执行main方法时设置成true
    public final static boolean IS_RUNNING_IN_IDE = false;
    // 默认端口号
    private static int port = 80;

    private final WebServer server;

    public static void main(String[] anArgs) throws Exception {
        if (anArgs != null && anArgs.length > 0) {
            final String portStr = anArgs[0];
            try {
                port = Integer.parseInt(portStr);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        new Main().start();
    }

    public Main() {
        this(port, IS_RUNNING_IN_IDE);
    }

    public Main(int port, boolean isRunningInIDE) {
        this.server = new WebServer(port, isRunningInIDE);
    }

    public void start() throws Exception {
        this.server.start();
        this.server.join();
    }
}
