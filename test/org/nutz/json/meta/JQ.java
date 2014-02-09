package org.nutz.json.meta;

import org.nutz.json.JsonField;

public class JQ {
	
	public JQ() {
	}

	public JQ(int age, double temp, float hz) {
		super();
		this.age = age;
		this.temp = temp;
		this.hz = hz;
	}

	@JsonField(null_int=150)
	private int age;
	@JsonField(null_double=-255)
	private double temp;
	@JsonField(null_double=-1)
	private float hz;
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public double getTemp() {
		return temp;
	}
	public void setTemp(double temp) {
		this.temp = temp;
	}
	public float getHz() {
		return hz;
	}
	public void setHz(float hz) {
		this.hz = hz;
	}
}
