package org.nutz.lang.hardware;

import org.nutz.lang.Strings;

public class NetworkItem {

    private String name;

    private String ipv4;

    private String ipv6;

    private String mac;

    private int mtu;

    private int signal;

    private String display;

    public boolean hasName() {
        return !Strings.isBlank(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String toString() {
        return String.format("%s:%s: (%s/%s) %s",
                             this.name,
                             this.mac,
                             this.ipv4,
                             this.ipv6,
                             this.display);
    }
}
