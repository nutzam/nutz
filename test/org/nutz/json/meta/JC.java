package org.nutz.json.meta;

public class JC {

	private IXX ixx = new IXX();
	
	public IXX getIxx() {
		return ixx;
	}
	
	class IXX {
		private int abc = 1;
		
		public int getAbc() {
			return abc;
		}
	}
}
