package com.letv.mocker.framework.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {
    /**
     * @Title: getIpAddr @author kaka www.zuidaima.com @Description: 获取客户端IP地址 @param @return @return
     *         String @throws
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (final UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    public static String getHost(String url) {
        if (url == null || url.trim().equals("")) {
            return "";
        }
        String host = "";
        final Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        final Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }

    public static String getHostAndPort(String url) {
        if (url == null || url.trim().equals("")) {
            return "";
        }
        String host = "";
        final Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+(\\:\\w+)?");
        final Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }

    public static String getLocalIPAddr() throws SocketException {
        final Enumeration<?> allNetInterfaces = NetworkInterface
                .getNetworkInterfaces();
        InetAddress ip = null;
        String ipStr = null;
        outer: while (allNetInterfaces.hasMoreElements()) {
            final NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
                    .nextElement();
            // System.out.println(netInterface.getName());
            final Enumeration<?> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    ipStr = ip.getHostAddress();
                    // System.out.println("本机的IP = " + ipStr);
                    if (!"127.0.0.1".equals(ipStr)) {
                        break outer;
                    }
                }
            }
        }
        return ipStr;

    }

    public static void main(String[] args) {
        System.out.println(getHost("http://10.200.91.32:8080/"));
        System.out.println(getHostAndPort("http://10.200.91.32:8080/"));

        System.out.println(getHost("http://10.200.91.33/"));
        System.out.println(getHostAndPort("http://10.200.91.33/"));
    }
}
