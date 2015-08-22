@echo off
SET TOMCAT_HOME=C:\Program Files\Apache Software Foundation\Tomcat 6.0
SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_16
SET DEPS=D:\home\nutz\deps
SET OUTPUT=D:\home\nutz\jars
SET PROJECT_HOME=D:\home\zozoh\workspace\svn\google.nutz\trunk

cd /d %PROJECT_HOME%\build
ant

@echo on
