package org.nutz.el.issue411;

public class Issue411 {
	public static class A{
        B b=new B();

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }
    public static class B{
        public boolean isPass(String a){
              return true;
        }
    }

}
