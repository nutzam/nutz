package org.nutz.trans;

import java.sql.Connection;

/**
 * 暴露事务管理的方法,以便用户不通过匿名内部类来使用Trans<p/>
 * <b>务必使用try-catch-final方式进行调用</b><p/>
 * <p/><p/>调用格式<p/>
 * <pre>{@code
try {
	Trans2.begin();
	//你的代码
	Trans2.commit();
} catch (Exception e) {
	Trans2.rollback();
} finally {
	Trans2.close();
}	
 * </pre>
*/
public final class Trans2 {

	public static void begin() throws Exception {
		Trans._begain(Connection.TRANSACTION_READ_COMMITTED);
	}
	
	public static void begin(int level) throws Exception {
		Trans._begain(level);
	}
	
	public static void commit() throws Exception {
		Trans._commit();
	}
	
	public static void rollback() throws Exception {
		Integer c = Trans.count.get();
		if (c == null)
			c = Integer.valueOf(0);
		Trans._rollback(c);
	}
	
	public static void close() throws Exception {
		Trans._depose();
	}
	
	/*
	public static void main(String[] args) throws Exception {
		try {
			Trans2.begin();
			
			Trans2.commit();
		} catch (Exception e) {
			Trans2.rollback();
		} finally {
			Trans2.close();
		}
	}
	*/
}
