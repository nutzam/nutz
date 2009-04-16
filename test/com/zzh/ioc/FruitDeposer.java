package com.zzh.ioc;

public class FruitDeposer implements Deposer<Fruit> {

	@Override
	public void depose(Fruit obj) {
		obj.setPrice(-2);
	}

}
