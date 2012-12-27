#### 这是什么

Nutz测试用web应用，使用[Sinatra](http://www.sinatrarb.com/)编写，已发布到[Heroku](http://nutztest.herokuapp.com)

#### 为什么需要这个

因为Nutz的测试代码里面，会在测试前启动一个`Jetty`的web服务才能跑完测试，但是该web服务没法通过Maven的测试（在命令行中运行`mvn test`）。所以就写了那么一个玩意。

#### 如何在本地运行

* 首先你需要个ruby环境

* 在命令行中运行`bundle install`安装所需要的包
  如果提示有错误，请先运行`gem install bundler --no-ri --no-rdoc`

* 在命令行中输入`ruby app.rb`，等服务启动后就可以通过[http://localhost:4567](http://localhost:4567)这个地址进行访问了

#### Tips
因为这个是个云端服务，有可能因为众所周知的原因无法访问该url而造成测试无法通过，这时候应该把`HttpTest.java`跟`BaseWebappTest.java`这两个类里面的url给指定成本地服务
