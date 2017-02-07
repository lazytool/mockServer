<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>jetty index jsp</title>
</head>
<body>
  这个jsp文件没有业务上的作用,jetty jsper会在把mocker.war包解压到当前目录下的work/目录下, 如果没有该目录，会默认解压到/tmp目录下。
  解压后会去查找webapp/index.jsp文件。
  若启动jetty时报错 如：
  十二月 15, 2016 7:59:47 下午 org.apache.jasper.servlet.JspServlet serviceJspFile
  严重: PWC6117: File "%2Fdata%2Fmocker%2Fwork%2Fjetty-0.0.0.0-80-webapp-_-any-%2Fwebapp%2Findex.jsp" not found
  应该先检查源码包webapp下是否有index.jsp文件，若没有在源码中添加该文件；
  再看是否为linux定期清空/tmp目录导致解压包文件丢失,可以在mocker-1.0-snapshot.jar同级目录下创建work目录，jetty就会把war包解压到work目录下，从而避免被清空。
</body>
</html>