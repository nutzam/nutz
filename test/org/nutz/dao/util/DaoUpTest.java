package org.nutz.dao.util;

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
url=jdbc:mysql://127.0.0.1/nutz
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

    // TODO 继续写带Pojo的简单操作及3种关联关系的操作
}
