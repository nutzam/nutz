package org.nutz.lang.hardware;

public class NetworkItem {

    private String ipv4;

    private String ipv6;

    private String mac;
    
    private int mtu;
    
    private int signal;

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getIpv6() {
        return ipv6;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

	public int getMtu() {
		return mtu;
	}

	public void setMtu(int mtu) {
		this.mtu = mtu;
	}

	public int getSignal() {
		return signal;
	}
	
	public void setSignal(int signal) {
		this.signal = signal;
	}
}
