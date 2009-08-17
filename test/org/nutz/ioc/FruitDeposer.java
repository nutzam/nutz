package org.nutz.ioc;

public class FruitDeposer implements ObjCallback<Fruit> {

	@Override
	public void invoke(Fruit obj) {
		obj.setPrice(-2);
	}

}
