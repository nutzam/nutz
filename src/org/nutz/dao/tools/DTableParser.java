package org.nutz.dao.tools;

import java.util.List;

/**
 * 将解析下面格式的数据表定义文件， 井号开始的为注释行
 * <p>
 * 每个数据表按照格式:
 * 
 * <pre>
 * 表名 {
 *    字段名   字段类型    [PK|+|!|&tilde;;|UNIQUE] &lt;默认值&gt;[,]
 * 	...
 *  }
 * </pre>
 * 
 * 其中：
 * <ul>
 * <li>+ 表示自增长
 * <li>! 表示 NOT NULL
 * <li>~ 表示 无符号
 * <li>如果 PK，那么 UNIQUE 和 ! 可以省略
 * </ul>
 * 例如:
 * 
 * <pre>
 * t_pet {
 * 	id INT +PK,
 * 	name VARCHAR(20),
 * 	masterId
 * }
 * 
 * t_master {
 * 	id INT +PK,
 * 	name VARCHAR(20)
 * }
 * 
 * t_food {
 * 	id INT +PK,
 * 	name VARCHAR(20) &lt;BANANA&gt;
 * }
 * 
 * t_pet_food {
 * 	petId INT,
 * 	foodId INT
 * }
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public interface DTableParser {

	List<DTable> parse(String str);

}
