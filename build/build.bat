@echo off
SET TOMCAT_HOME=D:\webapp-server\apache-tomcat-6.0.20
SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_16
SET DEPS=D:\study-workspace\Nutz-1.b.43\����Դ������Ҫ���ļ� 1.b.43
SET OUTPUT=D:\home\nutz\jars
SET PROJECT_HOME=D:\study-workspace\nutz

D:
cd %PROJECT_HOME%\build
ant

@echo on
