package org.nutz.dao.nst.test;

import java.util.List;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.test.DaoCase;

/**
 * TODO fix: 这个类的testcase不完整,无法运行
 * @author wendal
 *
 */
public abstract class DaoNstTest extends DaoCase {

	@Test
	public void eqTest() {
		List<A> list = dao.query(A.class, Cnd.where("aaa", "=", Cnd.nst(dao).select("aaa", A.class,
				Cnd.where("aid", "=", Cnd.nst(dao).select("aid", A.class, Cnd.where("aname", "=", "a1"))))));
		System.out.println(list);
	}

	@Test
	public void notEqTest() {
		List<A> list = dao.query(A.class, Cnd.where("aid", "<>", Cnd.nst(dao).select("aid", A.class,
				Cnd.where("aid", "not in", Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", ">", "1"))))));
		System.out.println(list);
	}

	@Test
	public void otherSymbolTest() {
		List<A> list1 = dao.query(A.class, Cnd.where("aid", ">", Cnd.nst(dao).select("aid", A.class,
				Cnd.where("aid", "not in", Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", ">", "1"))))));
		System.out.println(list1);
		List<A> list2 = dao.query(A.class, Cnd.where("aid", ">=", Cnd.nst(dao).select("aid", A.class,
				Cnd.where("aid", "not in", Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", ">", "1"))))));
		System.out.println(list2);
		List<A> list3 = dao.query(A.class, Cnd.where("aid", "<", Cnd.nst(dao).select("aid", A.class,
				Cnd.where("aid", "not in", Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", ">", "1"))))));
		System.out.println(list3);
		List<A> list4 = dao.query(A.class, Cnd.where("aid", "<=", Cnd.nst(dao).select("aid", A.class,
				Cnd.where("aid", "not in", Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", ">", "1"))))));
		System.out.println(list4);
	}

	@Test
	public void likeTest() {
		List<A> list1 = dao.query(A.class,
				Cnd.where("aaa", "like", Cnd.nst(dao).select("aaa", A.class, Cnd.where("aid", "=", 999))));
		System.out.println(list1);
		List<A> list2 = dao.query(A.class,
				Cnd.where("aaa", "not like", Cnd.nst(dao).select("aaa", A.class, Cnd.where("aid", "=", 999))));
		System.out.println(list2);
	}

	@Test
	public void inTest() {
		List<A> list1 = dao.query(A.class, Cnd.where("aid", "in", Cnd.nst(dao).select("aid", A.class, Cnd.where("aaa",
				"not in", Cnd.nst(dao).select("distinct aaa", A.class, Cnd.where("aaa", "=", "a1"))))));
		System.out.println(list1);
		List<A> list2 = dao.query(A.class, Cnd.where("aid", "not in", Cnd.nst(dao).select("aid", A.class, Cnd
				.where("aaa", "not in", Cnd.nst(dao).select("distinct aaa", A.class, Cnd.where("aaa", "=", "a1"))))));
		System.out.println(list2);
	}

	@Test
	public void existsTest() {
		List<A> list1 = dao.query(A.class, Cnd.where("", "exists", Cnd.nst(dao).select("aid", A.class, Cnd.where("aaa",
				"not in", Cnd.nst(dao).select("distinct aaa", A.class, Cnd.where("aaa", "=", "a1"))))));
		System.out.println(list1);
		List<A> list2 = dao.query(A.class, Cnd.where("", "not exists", Cnd.nst(dao).select("aid", A.class, Cnd
				.where("aaa", "not in", Cnd.nst(dao).select("distinct aaa", A.class, Cnd.where("aaa", "=", "a1"))))));
		System.out.println(list2);
	}

	@Test
	public void andTest() {
		dao.query(A.class, Cnd.where("", "exists",
				Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", "=", 1).and("aname", "like", "%a%"))));
		dao.query(A.class, Cnd.where("aid", "=", Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", "=", 1)))
				.and("aname", "=", "a1"));
		dao.query(A.class,
				Cnd.where("aid", "=",
						Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", "=", 1).and("aname", "=", "a1")))
						.and("aname", "=", "a1"));
	}

	@Test
	public void ascTest() {
		dao.query(A.class, Cnd.where("aid", "!=", 1).asc("aaa"));
		dao.query(A.class,
				Cnd.where("aid", "in", Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", "!=", 1).asc("aaa"))));
	}

	@Test
	public void limitTest() {
		dao.query(A.class, Cnd.where("aid", "!=", 1).limit(2, 1));
		// 我的数据库版本不支持子查询分页 不过能成功生成期望语句
		dao.query(A.class,
				Cnd.where("aid", "in", Cnd.nst(dao).select("aid", A.class, Cnd.where("aid", "!=", 1).limit(2, 1))));
	}

}
