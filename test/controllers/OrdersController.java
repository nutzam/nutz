package controllers;

import org.nutz.dao.Dao;

public class OrdersController {

	public static boolean SCOFFOLDING = true;
	private Dao dao;
	public void setDao(Dao dao){
		this.dao = dao;
	}
}

