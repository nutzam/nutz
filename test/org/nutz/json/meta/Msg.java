package org.nutz.json.meta;
import org.nutz.json.Json;

/**
 * 消息<br/>
 * 用于流程判断和用户提示
 * 
 * @author pengqirong
 */
public class Msg {

	/**
	 * 消息编码<br/>
	 * 用于流程判断
	 */
	public String code;

	/**
	 * 消息描述
	 */
	public String describe;
	
	/**
	 * 消息体
	 */
	public Msg(String code, String describe)
	{
		this.code = code;
		this.describe = describe;
	}

	@Override
	public String toString() {
		return Json.toJson(this);
	}
}