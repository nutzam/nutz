package org.nutz.dao.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.meta.SimplePojo;
import org.nutz.dao.util.meta.SystemJob;
import org.nutz.dao.util.meta.SystemTeam;
import org.nutz.dao.util.meta.SystemUser;
import org.nutz.lang.random.R;
import org.nutz.lang.random.StringGenerator;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * DaoHelper的TestCase及文档,这个类的信息都是源码里面!!!
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class DaoUpTest extends Assert {
    
    /** 如果你要在main方法中玩,这样写

    public void static main(String[] args) throws Exception {
        DaoUp.me().init("db.properties");
        Dao dao = DaoUp.me().dao();
        
        
        
        // 如果这是整个程序的结束,执行
        DaoUp.me().close();
    }
     */
    
    /**
     * Oracle用户请务必加入druid的jar包,不然肯定报错
     */
   
    private static final Log log = Logs.get(); // 这是获取Nutz的日志封装类的方法,你喜欢就用,不喜欢就用log4j的Logger或者System.out.println都可以.
    

    /**
     * 程序启动, 初始化DaoHelper
     */
    @BeforeClass
    public static void setUp() throws Exception {
        // 请在src或maven的resources下面添加一个文件叫nutz-test.properties
        // 内容类似于
        /**
url=jdbc:mysql://127.0.0.1/walnut
username=root
password=root
         */
        // 且加入了对应的数据库驱动, 混熟了可以加入druid和log4j(务必把log4j.properties弄好且日志为debug级)
        // 程序启动的时候调用*一次*就够了!!!!!!!!!!!!!!
        DaoUp.me().init(DaoUpTest.class.getClassLoader().getResourceAsStream("nutz-test.properties"));
        // 请留意nutz输出的日志,如果没有日志输出,那就肯定是log4j没配置好, 建议删掉log4j然后继续.
        

        // 提醒再提醒: 绝大部分情况下不需要new DaoHelper的!!
        
        // 如果是其他数据源(连接池), 新建对应的DataSource后如下初始化
        /**
        DaoUp.me().setDataSource(dataSource);
        */
        
        // 如果是古董项目, 通过某个静态方法获取连接, 可以创建个匿名内部类初始化
        /**
        DaoUp.me().setDataSource(new DataSource() {
            public Connection getConnection() throws SQLException {
                return XXXXX.getConnection(); // XXXXX.getConnection()代表遗留项目中获取数据库连接的方法
            }
            // 这个接口还有一些其他方法,全部默认实现就可以了,不会调用到
        });
        */
    }

    /**
     * 程序即将退出,关闭DaoHelper
     * @throws Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        // 提醒再提醒,这个千万千万别乱调用
        // 这个操作是关掉数据源的
        DaoUp.me().close();
        // 程序关闭前调用一次就够了!!!
    }

    /**
     * 1. 不使用pojo类,直接操作数据库, Chain插入,查询结果为Record(一个实现了Map接口的增强版LinkedHashMap).<p/>
     * 在nutz 1.b.38之后,均可使用类Map对象进行CRUD.<p/>
     * Chain代表插入/更新时的目标值K-V<p/>
     * Record是查询的返回类型,增强版的忽略大小写的LinkedHaskMap
     */
    @Test
    // 提醒一句,下面的assert均为Junit的方法
    public void test_without_pojo() {
        // 首先,得到Dao实例, 因为Junit的规则就是setUp先执行,所以Dao已经初始化好了
        Dao dao = DaoUp.me().dao(); // me()是public静态方法,意味着你可以从任何代码访问到哦
        
        // 弱弱地定义个表名方便操作
        String tableName = "tx_daoup_user";
        
        // 看看有无tx_text_user表,有的话就删掉好了
        if (dao.exists(tableName)) { // 没有openSession之类的事
            dao.drop(tableName); // 生产环境可别乱调用,死人了别找我
        }
        
        // 好了,现在开始建表, 其中的Sqls是Nutz中的自定义SQL的帮助类, 具体用法后面还有讲解
        // 为了最大兼容各种数据库,这里就建3个属性,不自增,没默认值
        // 如果您测试的数据库不支持下面的建表sql,自己改一下吧,呵呵.
        dao.execute(Sqls.create("create table " + tableName + " (id int, nm varchar(50), age int)"));
        
        // 首先,插入4条记录, 不抛异常就是执行成功(Dao接口的方法均如此)
        dao.insert(tableName, Chain.make("id", 1).add("nm", "wendal").add("age", 30));
        dao.insert(tableName, Chain.make("id", 2).add("nm", "zozoh").add("age", 60));
        dao.insert(tableName, Chain.make("id", 3).add("nm", "pangwu").add("age", 20));
        dao.insert(tableName, Chain.make("id", 4).add("nm", "ywjno").add("age", 10));
        
        // 我们统计一下是不是真的4条呢?
        assertEquals(4, dao.count(tableName)); //count方法可以查Pojo所代表的表,也可以直接写表名
                                                // 类似的方法还有fetch/query/clear等

        // ------------------
        // --------- query操作
        
        // 现在查一下小于25,且按nm降序
        List<Record> users = dao.query(tableName, Cnd.where("age", "<", 25).desc("nm")); // Cnd是最常用的查询构建类
        // 理应是2个人
        assertEquals(2, users.size()); // 忍不住提醒一句啊, 统计总数用dao.count, 因为真的有人这样写: return dao.query(XXX.class, Cnd....).size();
        
        // 因为是倒序,那第一个就是ywjno,第二个是pangwu.
        // 注意一下Record里面的key都是自动转为小写的,但值不会,这个可以放心.
        assertEquals("ywjno", users.get(0).getString("nm")); // ywjno就是冬日温泉,nutz的其中一位commiter
        assertEquals("pangwu", users.get(1).getString("nm")); // pangwu,胖五也, 也是commiter
        
        // 查大于15岁,且第二页,每页2条记录, 注意页数是从1开始的,若填了0,就不分页,全部记录
        users = dao.query(tableName, Cnd.where("age", ">", 15), dao.createPager(2, 2));
        // 因为大于15岁的只有3个人,又分页,所以结果应该是只有第3条记录
        assertEquals(1, users.size());
        assertEquals("pangwu", users.get(0).get("nm")); // ^|_|^ ...
        
        // --------------------------
        // --------- update和fetch操作
        
        //现在, 我们更新wendal的年龄,使其变成26,啊啊啊
        dao.update(tableName, Chain.make("age", 26), Cnd.where("nm", "=", "wendal")); 
        // Chain是链式的可以继续接下去哦
        // Chain.make(....).add(...).add(...)
        // 或者从一个Map/Pojo转化而来 Chain.from(xxxMap) Chain.from(obj)
        // 检查一下wendal的年龄,应该是26
        assertEquals(26, dao.fetch(tableName, Cnd.where("nm", "=", "wendal")).getInt("age"));
        
        // 然后让pangwu老一岁,注意是用makeSpecial,而且值是特殊的+1
        dao.update(tableName, Chain.makeSpecial("age", "+1"), Cnd.where("nm", "=", "pangwu"));
        // 同样要检查一下pangwu的年龄是不是变成 20+1=21岁了
        assertEquals(21, dao.fetch(tableName, Cnd.where("nm", "=", "pangwu")).getInt("age"));
        
        // 再大发慈悲,让zozoh年轻10岁吧.
        dao.update(tableName, Chain.makeSpecial("age", "age-10"), Cnd.where("nm", "=", "zozoh"));
        // 同样要检查一下zozoh的年龄是不是变成 60-10=50岁了
        assertEquals(50, dao.fetch(tableName, Cnd.where("nm", "=", "zozoh")).getInt("age"));
        
        // 为了表达我的博爱,全部人减5岁!!
        // 提示一下, makeSpecial属于直接拼入sql, 请留意注入问题.
        dao.update(tableName, Chain.makeSpecial("age", "age-5"), null);
        
        // ----------------------
        // 最后是delete和clear操作,然而delete是针对单个pojo对象删除的,所以这里只演示clear
        
        // 首先,我们删掉zozoh,原因嘛, 没你帅/漂亮.
        dao.clear(tableName, Cnd.where("nm", "=", "zozoh"));
        // 应剩下3条记录
        assertEquals(3, dao.count(tableName));
        
        // 现在再干掉年龄少于20岁的
        dao.clear(tableName, Cnd.where("age", "<", 20));
        // 应剩下1条记录
        assertEquals(1, dao.count(tableName));
        
        // 最后,全部杀光, 啊啊啊, 减少人口啊啊啊...
        dao.clear(tableName);
        // 应剩下0条记录, 没人了...
        assertEquals(0, dao.count(tableName));
        
        // 最后的最后,人在表在, 人没了,表也干掉
        dao.drop(tableName); // 三思三思... 
    }
    
    /**
     * 2. 带Pojo的基本操作,单表无操作
     */
    @Test
    public void test_pojo() {
        // 首先,得到Dao实例, 同理, 已经初始化好了的
        Dao dao = DaoUp.me().dao(); // 因为
        
        // 关于SimplePojo, 请务必打开看一眼哦
        // 这个类同时标注了@Id和@Name,自动建表时以@Id作为主键,@Name作为唯一键
        
        // 强制建表
        dao.create(SimplePojo.class, true); // 真实代码可别写true,被删表了别找我!!!
        
        // 先生成个随机字符串帮助实例, R是nutz内部的随机数相关的帮助类,有UUID, UU32, UU64等很多东西哦
        StringGenerator sg = R.sg(10); // 代表一个字符串长度为10的随机字符串生成器
        // 插入几条记录
        for (int i = 0; i < 100; i++) {
            // 如果用的是Oracle数据库死在这里了,请加上druid,使其用上连接池.
            dao.insert(new SimplePojo(sg.next(), "http://www." + sg.next() + ".cn", R.random(10, 100)));
        }
        // 统计一下,应该是100条
        assertEquals(100, dao.count(SimplePojo.class));
        
        // 批量插入1000条吧
        List<SimplePojo> list = new ArrayList<SimplePojo>();
        for (int i = 0; i < 100; i++) {
            list.add(new SimplePojo(sg.next(), "http://www." + sg.next() + ".cn", R.random(10, 100)));
        }
        dao.fastInsert(list); // 注意, list里面的对象必须是同一个类型哦
                              // fastInsert的特点是快,但不会执行@Prev和@Next, 也不会取回生成的主键哦,有得有失嘛
        
        // 看看大于45岁的有多少人,虽然理论上是一半一半,事实上经常不是这样...
        int re = dao.count(SimplePojo.class, Cnd.where("age", ">", 45));
        log.infof("older than 45y : %d", re);
        
        // 分页查询,跟无Pojo时的操作差不多
        List<SimplePojo> pojos = dao.query(SimplePojo.class, Cnd.where("age", ">", 45), dao.createPager(2, 10));
        
        log.infof("size=%d", pojos.size()); // 肯定小于等于10
        
        // 更新操作
        SimplePojo pojo = dao.fetch(SimplePojo.class); // 不加参数的话,就是取出第一条记录了
        assertNotNull(pojo); // 肯定不为null啦
        
        pojo.setWebsite("http://nutzbook.wendal.net"); // nutzbook是一本很值得看的nutz书哦
        dao.update(pojo, "website"); // 只需要更新website, 如果全部属性都更新就 dao.update(pojo, null)
        
        // 检查一下是不是真的变成nutzbook的网址了
        assertEquals("http://nutzbook.wendal.net", dao.fetch(SimplePojo.class, pojo.getId()).getWebsite());
        
        // 现在, 随便删掉一条记录
        dao.delete(SimplePojo.class, 20); // 传入的是数值,所以SimplePojo必须有个@Id的属性
        assertNull(dao.fetch(SimplePojo.class, 20)); // 肯定删了,或者根本没有,呵呵
        
        // 再根据name删掉一条
        dao.delete(SimplePojo.class, pojo.getName()); // 传入的是字符串,所以SimplePojo必须有个@Name
        assertNull(dao.fetch(SimplePojo.class, pojo.getName()));
        
        // delete方法时删除单条记录,而批量呢? clear方法
        
        // 开始大开杀戒
        dao.clear(SimplePojo.class, Cnd.where("id", "<", 20)); // 删掉所有id小于20的记录,哈哈
        
        // 统计一下20以内的记录,应该是0哦
        assertEquals(0, dao.count(SimplePojo.class, Cnd.where("id", "<", 20)));
        
        // 现在,让id大于50的记录的website通通变成nutzbook,哈哈哈
        int count = dao.update(SimplePojo.class, Chain.make("website", "http://nutzbook.wendal.net").add("createTime", new Date()), Cnd.where("id", ">", 50));
        assertEquals(count, dao.count(SimplePojo.class, Cnd.where("id", ">", 50).and("website", "=", "http://nutzbook.wendal.net")));
        // 请留意一下Cnd和Chain的链式写法
        
        // 按某人的建议, 查询一下id大于30或者website为nutzbook且name包含a的记录
        // 翻译成伪sql就是  where id > 300 or (website like "%nutzbook%" and name like "%a%")
        int size = dao.count(SimplePojo.class, Cnd.where("id", ">", 300).or(Cnd.exps("website", "like", "%nutzbook%").and("name", "like", "%a%")));
        assertTrue(size > 0);
        // sql 输出类似于
        // SELECT COUNT(*) FROM t_test_simple_pojo  WHERE id>300 OR (website LIKE '%nutzbook%' AND name LIKE '%a%')
        
        // 关于日志中的sql, 特别说明一下, nutz真正执行的sql是
        /**
         * SELECT COUNT(*) FROM t_test_simple_pojo  WHERE id>? OR (website LIKE ? AND name LIKE ?)
         */
        // 即PreparedStatement, 参数都是以安全的方式传输.
        // 而日志中带上参数的以"For example"提示的sql是用于显示的,并非真正执行的sql
        
        // 好了, happy完了,全杀
        dao.clear(SimplePojo.class, null);
        
    }

    // 3种关联关系的操作
    @Test
    public void test_links() {
        // 首先,得到Dao实例
        Dao dao = DaoUp.me().dao();
        
        // 3个Pojo的关系如下
        // SystemUser -----(1对1)---> SystemTeam
        // SystemUser -----(1对多)---> SystemJob
        // SystemTeam -----(多对多)---> SystemJob
        
        /*
         * 场景如下:
         * 有3个用户, 2个team, 25个任务
         * A用户属于Team A, 有10个任务
         * B用户属于Team B, 有10个任务
         * C用户属于Team B, 有5个任务
         */
        
        // 强制建表
        dao.create(SystemUser.class, true);
        dao.create(SystemTeam.class, true);
        dao.create(SystemJob.class, true);
        
        // 先塞点内容进去
        
        // 3个用户, wendal, zozoh, pangwu86
        SystemUser userA = new SystemUser();
        userA.setName("wendal");
        SystemUser userB = new SystemUser();
        userB.setName("zozoh");
        SystemUser userC = new SystemUser();
        userC.setName("pangwu86");
        
        // 2个组, sysadmin和root
        SystemTeam teamA = new SystemTeam();
        teamA.setName("sysadmin");
        SystemTeam teamB = new SystemTeam();
        teamB.setName("root");
        
        // 关联用户到不同的组
        userA.setTeam(teamA);
        userB.setTeam(teamB);
        userC.setTeam(teamB);

        List<SystemJob> jobsA = new ArrayList<SystemJob>();
        List<SystemJob> jobsB = new ArrayList<SystemJob>();
        List<SystemJob> jobsC = new ArrayList<SystemJob>();
        for (int i = 0; i < 10; i++) {
            SystemJob job = new SystemJob();
            jobsA.add(job);
            job = new SystemJob();
            jobsB.add(job);
            if (i < 5) {
                job = new SystemJob();
                jobsC.add(job);
            }
        }
        userA.setJobs(jobsA);
        userB.setJobs(jobsB);
        userC.setJobs(jobsC);
        
        dao.insertWith(userA, null);
        dao.insertWith(userB, null);
        userC.setTeamId(teamB.getId()); // 因为下一句中team不会插入,所以需要自行把关联字段设置一下
        dao.insertWith(userC, "jobs"); // 注意, team是已经插入过了,跟userB同一个team哦,所以只需要也只能插入jobs了
        
        // 判断一下已经插入的数据, 因为id是自增的,插入后关联对象也理应有值
        assertTrue(userA.getTeam().getId() > 0);
        assertTrue(userB.getTeam().getId() > 0);
        
        for (SystemJob job : userA.getJobs()) {
            assertTrue(job.getId() > 0);
        }
        for (SystemJob job : userB.getJobs()) {
            assertTrue(job.getId() > 0);
        }
        
        // 插入userA,userB, userC的时候, @One和@Many都插入了,但@ManyMany是team-->job的映射,并没有插入哦
        // 因为nutz只认单层单向映射
        
        // TeamA的任务,就是UserA的任务
        teamA.setJobs(new ArrayList<SystemJob>(userA.getJobs()));
        // TeamB的任务, 是UserB和UserC的任务的集合
        ArrayList<SystemJob> jobs = new ArrayList<SystemJob>();
        jobs.addAll(userB.getJobs());
        jobs.addAll(userC.getJobs());
        teamB.setJobs(jobs);
        
        // 现在插入@ManyMany的数据
        dao.insertRelation(teamA, null);
        dao.insertRelation(teamB, null);
        
        //---------------------------------------------------
        // 查询操作, fetch及fetchLinks
        // 关键词: 单向,无状态
        
        
        // 看看zozoh是谁
        SystemUser who = dao.fetch(SystemUser.class, "zozoh");
        assertNotNull(who);
        assertNull(who.getTeam()); // 注意,这是判断是null哦, 因为关联对象是不会主动取的
        assertNull(who.getJobs()); // 一样是null!!!
        
        // 为什么是null呢? 看这句
        assertTrue(SystemUser.class == who.getClass());
        // 为什么是相等的呢? 因为Nutz中的Pojo都是无状态的, 不存在托管/非托管的状态
        // 所以没有hibernate那种代理类的情况,所以数据库的字段也需要映射在具体的java属性中
        // 而非代理类的隐藏属性里面
        
        // 下面取出关联对象
        dao.fetchLinks(who, "jobs"); // 仅取出jobs
        assertNotNull(who.getJobs());
        dao.fetchLinks(who, null); // 全部取出, 观察日志,会发现team和jobs都会取
        // 为什么jobs都取过,还会再取一次呢? 因为无状态哦, nutz是不会记住这个对象的状态的

        assertNotNull(who.getJobs());
        assertNotNull(who.getTeam());
        
        // 那么, Team的关联对象呢?
        assertNull(who.getTeam().getJobs());
        // 原因是fetchLinks只读取一层
        
        // 同理, job.getUser()也会是null
        assertNull(who.getJobs().get(0).getUser());
        
        dao.fetchLinks(who.getJobs().get(0), null);
        // 现在, user.getJob().getUser 是否与 user是同一个对象呢?
        assertFalse(who == who.getJobs().get(0).getUser());
        
        // @ManyMany的取出操作是一个样
        assertNull(who.getTeam().getJobs());
        assertNotNull(dao.fetchLinks(who.getTeam(), null).getJobs());
        
        // 批量fetchLinks
        List<SystemUser> users = dao.query(SystemUser.class, null); // 要不加个条件判断一下谁最蛋疼?
        assertEquals(3, users.size());
        dao.fetchLinks(users, null);
        for (SystemUser user : users) {
            assertNotNull(user.getTeam());
            assertNotNull(user.getJobs());
        }
        
        // -------------------------------------------------------------------
        // 更新/删除关联对象/关联信息操作
        
        // @One更新, 把Team A的名字改成god --> 其实可以直接改... 只是为了演示...
        SystemUser wendal = dao.fetch(SystemUser.class, "wendal");
        wendal = dao.fetchLinks(wendal, "team"); // fetchLinks返回的就是原来的对象, 有返回值是为了方便链式调用
        assertNotNull(wendal.getTeam());
        
        wendal.getTeam().setName("god");
        dao.updateLinks(wendal, null);
        
        SystemTeam godTeam = dao.fetch(SystemTeam.class, "god");
        assertNotNull(godTeam);
        
        // @Many 更新
        
        SystemUser zozoh = dao.fetchLinks(dao.fetch(SystemUser.class, "wendal"), null);
        assertNotNull(zozoh.getJobs());
        
        // 把前面4个任务的state设置为1, 算是完成吧
        for (int i = 0; i < 4; i++) {
            zozoh.getJobs().get(i).setState(1);
        }
        dao.updateLinks(zozoh, "jobs");
        
        // 既然更新了,那看看未完成的任务是不是21个
        assertEquals(21, dao.count(SystemJob.class, Cnd.where("state", "=", 0)));
        
        
        // @ManyMany的更新
        // 单纯更新job表的数据, 与前面的@Many无异, 不再重复
        // 下面是要更新@ManyMany中的中间表
        
        SystemTeam rootTeam = dao.fetch(SystemTeam.class, "root");
        dao.fetchLinks(rootTeam, null);

        assertNotNull(rootTeam.getJobs());
        assertEquals(15, rootTeam.getJobs().size());
        
        // 移除前11个任务的引用
        for (int i = 0; i < 11; i++) {
            rootTeam.getJobs().remove(0);
        }
        dao.deleteLinks(rootTeam, null); // deleteLinks的特点就是当前对象引用的关联对象才会删除哦
        
        // 重新fetchLinks, 就剩下11个job了
        dao.fetchLinks(rootTeam, null);
        assertEquals(11, rootTeam.getJobs().size());
        
        
        // 现在,我们把pangw86这个用户及相关的job删除
        SystemUser pangwu86 = dao.fetch(SystemUser.class, "pangwu86");
        assertFalse(0 == dao.count(SystemJob.class, Cnd.where("userId", "=", pangwu86.getId())));
        dao.fetchLinks(pangwu86, "jobs");
        dao.deleteWith(pangwu86, "jobs"); // 因为team还不能删除,所以需要制定只删除jobs
        
        assertNull(dao.fetch(SystemUser.class, "pangwu86"));
        assertEquals(0, dao.count(SystemJob.class, Cnd.where("userId", "=", pangwu86.getId())));
        
        // ---------------------------------
        // 各种clear
        // 现在,天黑了,统统杀掉
        
        // 清除关联关系,直接全干掉
        zozoh = dao.fetchLinks(dao.fetch(SystemUser.class, "zozoh"), null);
        dao.clearLinks(zozoh, null);
        assertEquals(0, dao.count(SystemJob.class, Cnd.where("userId", "=", dao.fetch(SystemUser.class, "zozoh").getId())));
        assertNotNull(dao.fetch(SystemUser.class, "zozoh")); // 人还在
        dao.delete(zozoh); // 干掉...
        dao.clearLinks(zozoh.getTeam(), null); // 所在Team已经没人了,相关的job也清除掉
        
        // 连人带team带jobs全删!!
        wendal = dao.fetch(SystemUser.class, "wendal");
        SystemTeam team = dao.fetchLinks(wendal, "team").getTeam();
        
        dao.clearLinks(wendal, null); // 关联关系全删
        dao.delete(wendal); // 删掉自己
        // team与job的关联关系也需要清除
        dao.clearLinks(team, null); // 二级关联,也干掉
        
        assertNull(dao.fetch(SystemUser.class, "wendal")); // 人被删了...
        assertNull(dao.fetch(SystemTeam.class, "sysadmin")); // 组也删了
        // 即使数据库记录干掉了, wendal这个对象依然可用,因为完全跟数据库分离的
        assertEquals(0, dao.count(SystemJob.class, Cnd.where("userId", "=", wendal.getId())));
        
        
        // 应该都挂了吧? 检查一下
        assertEquals(0, dao.count(SystemUser.class));
        assertEquals(0, dao.count(SystemTeam.class));
        assertEquals(0, dao.count(SystemJob.class));
        // 等等, 别放过了关联表
        assertEquals(0, dao.count("t_daoup_team_job")); // 可以直接传表名
        
        // 好了, 世界清静了...
    }
    
    /**
     * 自定义SQL, 解决绝大部分Dao接口无法满足你的sql调用
     */
    @Test
    public void test_sql() {
        Dao dao = DaoUp.me().dao(); // 不再啰嗦, 不然你也烦了..
        
        // 首先,我们用前一个test的pojo建表
        dao.create(SystemUser.class, true);
        dao.create(SystemTeam.class, true);
        
        // 建1个用户,一个Team
        SystemUser user = new SystemUser();
        user.setName("wendal");
        SystemTeam team = new SystemTeam();
        team.setName("root");
        user.setTeam(team);
        dao.insertWith(user, "team");
        
        // Dao接口大部分操作都生成单表SQL, 除了@ManyMany.
        // 所以很多人觉得@One也查2次很浪费啊, 那这里写一条
        
        String str = "select u.*,t.* from $user_table u INNER JOIN $team_table t ON u.t_id = t.id where u.nm=@name";
        Sql sql = Sqls.create(str); // 创建一个自定义Sql对象, 还有各种形式哦
        sql.vars().set("user_table", dao.getEntity(SystemUser.class).getTableName()); // $开头的是变量,会直接拼入sql哦
        sql.vars().set("team_table", dao.getEntity(SystemTeam.class).getTableName()); // 同上
        sql.params().set("name", "wendal"); // @开头的是参数, 会变成问号哦
        sql.setCallback(Sqls.callback.record()); // Sqls.callback提供N种常用的回调,你需要的一般都在里面了
        dao.execute(sql); // 注意, execute是没有返回值的, 所需要的值都通过callback来获取
                          // 不抛出异常就是执行成功
        Record record = sql.getObject(Record.class); //Sqls.callback.record()的返回值就是Record
        // 从Record还原出Pojo来
        SystemUser u = record.toEntity(dao.getEntity(SystemUser.class), "u.");
        assertNotNull(u);
        SystemTeam t = record.toEntity(dao.getEntity(SystemTeam.class), "t.");
        assertNotNull(t);
        u.setTeam(t);
    }
    
    /**
     * 自定义SQL, 进阶, 自定义回调
     */
    @Test
    public void test_custom_sql() {
        Dao dao = DaoUp.me().dao();
        // 建表,准备数据
        dao.create(SystemUser.class, true);
        SystemUser user = new SystemUser();
        user.setName("wendal");
        dao.fastInsert(user);
        user = new SystemUser();
        user.setName("zozoh");
        dao.fastInsert(user);
        
        Sql sql = Sqls.create("select id from $table $condition");
        sql.setEntity(dao.getEntity(SystemUser.class)); // 设置之后, setCondition里面的条件就可以用java属性名了
        sql.setCondition(Cnd.where("name", "=", "zozoh"));
        
        sql.vars().set("table", dao.getEntity(SystemUser.class).getTableName()); // 只是为了演示变量设置, 你可以直接在sql里写好表名
        sql.getContext().attr("hi-我是自定义上下文变量", "哈哈哈哈");
        sql.setCallback(new SqlCallback() { // 同样的功能可以用内置的Sqls.callback.integer();
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                assertEquals(sql.getContext().attr("hi-我是自定义上下文变量"), "哈哈哈哈");
                if (rs.next())
                    return rs.getInt(1);
                return -1;
                // 这里的conn和rs不需要用户代码关闭哦.
                // 你可以通过sql.
            }
        });
        dao.execute(sql);
        assertEquals(2, sql.getInt());// 还有sql.getXXXX等等方法等着你哦
    }
    
    /**
     * 操作数据库连接
     * @throws SQLException 
     */
    @Test
    public void test_connection() throws SQLException {
        // 有2种方式,看你喜欢
        
        // 第一种, 在Dao接口下执行
        Dao dao = DaoUp.me().dao();
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                // 做任何你想做的jdbc操作,但最好别关闭这个conn, 因为nutz会为你处理好
                // 如果当前上下文是事务,那这个连接就是事务那个连接
            }
        });
        
        // 第二种,不经过Dao,直接从DataSource. 如果是Mvc应用,请通过注入获取DataSource
        DataSource ds = DaoUp.me().getDataSource();
        Connection conn = null;
        try {
            conn = ds.getConnection();
            // 做爱做的事吧 ^_^
        }
        finally {
            try {
                if (conn != null)
                    conn.close(); // 务必关闭连接!!!
            }
            catch (Throwable e) {
                log.debug("fail to close Connection", e);
            }
        }
    }
}
