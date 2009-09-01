package org.nutz.trans;

import java.sql.Connection;
import java.sql.SQLException;

import org.nutz.lang.Lang;

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

	private static void begain(int level) throws Exception {
		if (null == trans.get()) {
			Transaction tn = null == implClass ? new NutTransaction() : implClass.newInstance();
			tn.setLevel(level);
			trans.set(tn);
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
			try {
				trans.get().resetTransactionLevel();
			} catch (SQLException e) {
				throw Lang.wrapThrow(e);
			} finally {
				trans.set(null);
			}
	}

	private static void rollback(Integer num) {
		count.set(num);
		if (count.get() == 0)
			trans.get().rollback();
	}

	public static void exec(Atom... atoms) {
		exec(Connection.TRANSACTION_READ_COMMITTED, atoms);
	}

	public static void exec(int level, Atom... atoms) {
		if (null == atoms)
			return;
		int num = count.get() == null ? 0 : count.get();
		try {
			begain(level);
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
