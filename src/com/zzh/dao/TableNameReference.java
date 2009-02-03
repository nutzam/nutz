package com.zzh.dao;

public class TableNameReference {

	private static ThreadLocal<Object> object = new ThreadLocal<Object>();

	/**
	 * @return current reference object
	 */
	public static Object get() {
		return object.get();
	}

	/**
	 * @param obj
	 * @return the reference object set by current thread last time
	 */
	public static Object set(Object obj) {
		Object re = get();
		object.set(obj);
		return re;
	}

	/**
	 * This is a test method
	 */
	public static void main(String[] args) {
		TableNameReference.set(13);
		System.out.println(String.format("%d:%s", Thread.currentThread().getId(),
				TableNameReference.get()));
		Thread t = new Thread() {
			@Override
			public void run() {
				TableNameReference.set(74);
				System.out.println(String.format("%d:%s", Thread.currentThread().getId(),
						TableNameReference.get()));
			}
		};
		t.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("%d:%s", Thread.currentThread().getId(),
				TableNameReference.get()));
	}
}
