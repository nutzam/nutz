package com.zzh.ioc;

public class Fruit {

	private String name;
	private int price;
	private boolean onSale;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isOnSale() {
		return onSale;
	}

	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}

	public void destroy() {
		price = -1;
	}

	public void setAlias(String alias) {
		this.name = "[" + alias + "]";
	}

	public void setCost(int cost) {
		this.price = cost * 2;
	}

}
