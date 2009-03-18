package com.zzh.trans;

import java.sql.SQLException;

import com.zzh.lang.Lang;

public abstract class Trans {

	private static Class<? extends Transaction> implClass;

	static ThreadLocal<Transaction> trans = new ThreadLocal<Transaction>();
	static ThreadLocal<Integer> count = new ThreadLocal<Integer>();

	public static Transaction get() {
		return trans.get();
	}

	public static void setup(Class<? extends Transaction> classOfTransaction) {
		implClass = classOfTransaction;
	}

	private static void begain() throws Exception {
		if (null == trans.get()) {
			trans.set(null == implClass ? new NutTransaction() : implClass.newInstance());
			count.set(0);
		}
		count.set(count.get() + 1);
	}

	private static void commit() throws SQLException {
		count.set(count.get() - 1);
		if (count.get() == 0)
			trans.get().commit();
	}

	private static void depose() {
		if (count.get() == 0)
			trans.set(null);
	}

	private static void rollback(Integer num) {
		count.set(num);
		if (count.get() == 0)
			trans.get().rollback();
	}

	public static void exec(Atom... atoms) {
		if (null == atoms)
			return;
		int num = count.get() == null ? 0 : count.get();
		try {
			begain();
			for (Atom atom : atoms)
				atom.run();
			commit();
		} catch (Throwable e) {
			rollback(num);
			throw Lang.wrapThrow(e);
		} finally {
			depose();
		}
	}
}
