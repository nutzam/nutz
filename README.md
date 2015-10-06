### Nutz 

[![Build Status](https://travis-ci.org/nutzam/nutz.png?branch=master)](https://travis-ci.org/nutzam/nutz)
[![Circle CI](https://circleci.com/gh/nutzam/nutz/tree/master.svg?style=svg)](https://circleci.com/gh/nutzam/nutz/tree/master)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/4917/badge.svg)](https://scan.coverity.com/projects/4917/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz/)
[![codecov.io](http://codecov.io/github/nutzam/nutz/coverage.svg?branch=master)](http://codecov.io/github/nutzam/nutz?branch=master)
[![GitHub release](https://img.shields.io/github/release/nutzam/nutz.svg)](https://github.com/nutzam/nutz/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

[![Join the chat at https://gitter.im/nutzam/nutz](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nutzam/nutz?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


对于 Java 程序员来说，除 SSH 之外，的另一个选择

### Talk is cheap. Show me the code!!

### 项目目标

在力所能及的情况下，最大限度的提高 Web 开发人员的生产力。

### 项目各种资源地址

*   [项目官网](http://nutzam.com)
*   [Github](https://github.com/nutzam/nutz)
*   [Nutz社区](https://nutz.cn/yvr)
*   在线文档
    *   [官网](http://nutzam.com/core/nutz_preface.html)（发布新版本时更新）
    *   [GitHub Pages](http://nutzam.github.io/nutz/)（基本做到文档有变动就更新）
    *   [社区常见问答 Part 1](http://nutzam.github.io/nutz/faq/common_qa_1.html)（新手必看）
*   [视频+官方发布](http://downloads.nutzam.com/)
*   [各种插件](http://github.com/nutzam/nutzmore)
*   [好玩的Nutzbook](http://nutzbook.wendal.net) (引导式nutz入门书)
*	[在线javadoc](http://javadoc.nutz.cn)
*	[案例提交](https://github.com/nutzam/nutz/issues/819)  (企业项目及开源项目)
*	[短地址服务](http://nutz.cn) (贴日志贴代码很方便)

现已通过 Oracle JDK 8、Oracle JDK 7、OpenJDK 7、OpenJDK 6下的 maven 测试，请查阅 [Travis CI地址](https://travis-ci.org/nutzam/nutz)、 [CircleCI地址](https://circleci.com/gh/nutzam/nutz)

### Maven 资源

稳定发布版本

```xml
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz</artifactId>
			<version>1.b.53</version>
		</dependency>
```

快照版本在每次提交后会自动deploy到sonatype快照库,享受各种bug fix和新功能

```xml
	<repositories>
		<repository>
			<id>ossrh</id>
			<url>https://oss.sonatype.org/content/repositories/snapshots</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>
	<dependencies>
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz</artifactId>
			<version>1.r.54-SNAPSHOT</version>
		</dependency>
		<!-- 其他依赖 -->
	</dependencies>
```

也可以将repositories配置放入$HOME/.m2/settings.xml中

或者直接去[快照库下载](https://oss.sonatype.org/content/repositories/snapshots/org/nutz/nutz/1.r.54-SNAPSHOT/)

## Sponsorship

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of [YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) 
and [YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp),
innovative and intelligent tools for profiling Java and .NET applications.

![YourKit Logo](https://cloud.githubusercontent.com/assets/1317309/4507430/7119527c-4b0c-11e4-9245-d72e751e26ee.png)
