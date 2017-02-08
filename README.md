# mockServer
mocker server based on HTTP protocal

#使用方法
1.生成jar包

mvn package

2.启动服务

Linux：java -jar mocker-1.0-SNAPSHOT.jar

MAC：sudo java -jar mocker-1.0-SNAPSHOT.jar

服务默认使用80端口

3.访问mockServer配置页：

http://localhost/

选择record模式

4.修改要录制的服务器hosts文件,使其指向mockServer服务器。

5.目录清单

  mock_data mock数据目录

  mocker.db 数据库文件
  
  mocker-1.0-SNAPSHOT.jar 主要应用程序
  
  logs web访问日志
  
  ${HOME}/mocker_logs 应用详细日志
  
