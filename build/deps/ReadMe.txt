说明:
1. 这个文件夹下的文件,只是为了方便编译/测试而存放.减少用户下载源码后,寻找相关jar的时间.

junit-4.3.1.jar                              编译测试代码时使用
commons-dbcp-1.3.jar                         编译测试代码时使用
commons-pool-1.5.4.jar                       运行测试代码时使用
h2-1.2.131.jar                               测试数据库时使用,与示例的nutz-test.properties配套
servletapi-2.4.jar                           编译用,一般的J2EE都含有的
log4j-1.2.14.jar                             编译用,但运行时不是必须的

log4j.properties                             测试时使用的log4j配置文件,
nutz-test.properties                         测试时使用的数据库配置示例

测试时,可以将两个properties文件,移入test源文件夹,将这里的jar加入构建路径