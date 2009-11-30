SET JAVA_HOME=%JAVA_HOME%
SET NUTZ_HOME=.

SET JAVAASSIST_PATH=..\deps\javassist.jar
SET LOG4J_PATH=..\deps\log4j-1.2.15.jar
SET JUNIT_PATH=..\deps\junit-4.7.jar

SET CLASSPATH=%NUTZ_HOME%;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\jre\rt.jar
SET CLASSPATH=%CLASSPATH%;%JAVAASSIST_PATH%;%LOG4J_PATH%;%JUNIT_PATH%

%JAVA_HOME%\bin\java org.nutz.test.utils.SimpleRunner org.nutz.log.Log4jAdapterPerformanceTest:testLog4jCreation > log.performance.test.txt
%JAVA_HOME%\bin\java org.nutz.test.utils.SimpleRunner org.nutz.log.Log4jAdapterPerformanceTest:testNutzCreation >> log.performance.test.txt
%JAVA_HOME%\bin\java org.nutz.test.utils.SimpleRunner org.nutz.log.Log4jAdapterPerformanceTest:testLog4jOutput >> log.performance.test.txt
%JAVA_HOME%\bin\java org.nutz.test.utils.SimpleRunner org.nutz.log.Log4jAdapterPerformanceTest:testNutzOutput >> log.performance.test.txt

%JAVA_HOME%\bin\java org.nutz.test.utils.SimpleRunner org.nutz.log.JdkAdapterPerformanceTest:testJdkCreation >> log.performance.test.txt
%JAVA_HOME%\bin\java org.nutz.test.utils.SimpleRunner org.nutz.log.JdkAdapterPerformanceTest:testNutzCreation >> log.performance.test.txt
%JAVA_HOME%\bin\java org.nutz.test.utils.SimpleRunner org.nutz.log.JdkAdapterPerformanceTest:testJdkOutput >> log.performance.test.txt
%JAVA_HOME%\bin\java org.nutz.test.utils.SimpleRunner org.nutz.log.JdkAdapterPerformanceTest:testNutzOutput >> log.performance.test.txt
