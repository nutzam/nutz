package org.nutz.dao.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.util.meta.SimplePojo;
import org.nutz.dao.util.meta.SystemJob;
import org.nutz.dao.util.meta.SystemTeam;
import org.nutz.dao.util.meta.SystemUser;
import org.nutz.lang.random.R;
import org.nutz.lang.random.StringGenerator;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * DaoHelper的TestCase及文档
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class DaoUpTest extends Assert {
    
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
        // 首先,得到Dao实例
        Dao dao = DaoUp.me().dao();
        
        // 弱弱地定义个表名方便操作
        String tableName = "tx_test_user";
        
        // 看看有无tx_text_user表,有的话就删掉好了
        if (dao.exists(tableName)) {
            dao.drop(tableName);
        }
        
        // 好了,现在开始建表, 其中的Sqls是Nutz中的自定义SQL的帮助类
        // 为了最大兼容各种数据库,这里就建3个属性,不自增,没默认值
        // 如果您测试的数据库不支持下面的建表sql,自己改一下吧,呵呵.
        dao.execute(Sqls.create("create table " + tableName + " (id int, nm varchar(50), age int)"));
        
        // 首先,插入4条记录, 不抛异常就是执行成功
        dao.insert(tableName, Chain.make("id", 1).add("nm", "wendal").add("age", 30));
        dao.insert(tableName, Chain.make("id", 2).add("nm", "zozoh").add("age", 60));
        dao.insert(tableName, Chain.make("id", 3).add("nm", "pangwu").add("age", 20));
        dao.insert(tableName, Chain.make("id", 4).add("nm", "ywjno").add("age", 10));
        
        // 我们统计一下是不是真的4条呢?
        assertEquals(4, dao.count(tableName));

        // ------------------
        // --------- query操作
        
        // 现在查一下小于25,且按nm降序
        List<Record> users = dao.query(tableName, Cnd.where("age", "<", 25).desc("nm"));
        // 理应是2个人
        assertEquals(2, users.size());
        
        // 因为是倒序,那第一个就是ywjno,第二个是pangwu.
        // 注意一下Record里面的key都是自动转为小写的,但值不会,这个可以放心.
        assertEquals("ywjno", users.get(0).getString("nm"));
        assertEquals("pangwu", users.get(1).getString("nm"));
        
        // 查大于15岁,且第二页,每页2条记录, 注意页数是从1开始的,若填了0,就不分页,全部记录
        users = dao.query(tableName, Cnd.where("age", ">", 15), dao.createPager(2, 2));
        // 因为大于15岁的只有3个人,又分页,所以结果应该是只有第3条记录
        assertEquals(1, users.size());
        assertEquals("pangwu", users.get(0).get("nm"));
        
        // --------------------------
        // --------- update和fetch操作
        
        //现在, 我们更新wendal的年龄,使其变成26,啊啊啊
        dao.update(tableName, Chain.make("age", 26), Cnd.where("nm", "=", "wendal"));
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
        
        // 最后,全部杀光
        dao.clear(tableName);
        // 应剩下0条记录
        assertEquals(0, dao.count(tableName));
        
        // 最后的最后,人在表在, 人没了,表也干掉
        dao.drop(tableName);
    }
    
    /**
     * 2. 带Pojo的基本操作,单表无操作
     */
    @Test
    public void test_pojo_singal() {
        // 首先,得到Dao实例
        Dao dao = DaoUp.me().dao();
        
        // 强制建表
        dao.create(SimplePojo.class, true); // 真实代码可别写true,被删表了别找我!!!
        
        // 先生成个随机字符串帮助实例
        StringGenerator sg = R.sg(10);
        // 插入几条记录
        for (int i = 0; i < 100; i++) {
            dao.insert(new SimplePojo(sg.next(), "http://www." + sg.next() + ".cn", R.random(10, 100)));
        }
        // 统计一下,应该是100条
        assertEquals(100, dao.count(SimplePojo.class));
        
        // 看看大于45岁的有多少人,虽然理论上是一半一半,事实上经常不是这样...
        int re = dao.count(SimplePojo.class, Cnd.where("age", ">", 45));
        log.infof("older than 45y : %d", re);
        
        // 分页查询,跟无Pojo时的操作差不多
        List<SimplePojo> pojos = dao.query(SimplePojo.class, Cnd.where("age", ">", 45), dao.createPager(2, 10));
        
        log.infof("size=%d", pojos.size()); // 肯定小于等于10
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
        
        SystemUser userA = new SystemUser();
        userA.setName("wendal");
        SystemUser userB = new SystemUser();
        userB.setName("zozoh");
        SystemUser userC = new SystemUser();
        userC.setName("pangwu86");
        
        SystemTeam teamA = new SystemTeam();
        teamA.setName("sysadmin");
        SystemTeam teamB = new SystemTeam();
        teamB.setName("root");
        SystemTeam teamC = new SystemTeam();
        teamC.setName("admin");
        
        userA.setTeam(teamA);
        userB.setTeam(teamB);
        userC.setTeam(teamB);
        
        List<SystemJob> jobs = new ArrayList<SystemJob>();
        for (int i = 0; i < 10; i++) {
            SystemJob job = new SystemJob();
            job.setName(R.UU32());
            jobs.add(job);
        }
        userA.setJobs(jobs);
        
        jobs = new ArrayList<SystemJob>();
        for (int i = 0; i < 10; i++) {
            SystemJob job = new SystemJob();
            job.setName(R.UU32());
            jobs.add(job);
        }
        userB.setJobs(jobs);
        
        jobs = new ArrayList<SystemJob>();
        for (int i = 0; i < 5; i++) {
            SystemJob job = new SystemJob();
            job.setName(R.UU32());
            jobs.add(job);
        }
        userC.setJobs(jobs);
        
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
        jobs = new ArrayList<SystemJob>();
        jobs.addAll(userB.getJobs());
        jobs.addAll(userC.getJobs());
        
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
        assertNotEquals(who, who.getJobs().get(0).getUser());
        
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
        // 更新操作
        
        
    }
}
