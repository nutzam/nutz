package org.nutz.lang.hardware;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.lang.Strings;

/**
 * @author wendal
 * @author pw
 */
public class Networks {

    private static Map<NetworkType, String> ntMap = new HashMap<NetworkType, String>();

    static {
        ntMap.put(NetworkType.LAN, "eth, en");
        ntMap.put(NetworkType.WIFI, "wlan");
        ntMap.put(NetworkType.ThreeG, "ppp");
        ntMap.put(NetworkType.VPN, "tun");
    }

    public static Map<String, NetworkItem> networkItems() {
        Map<String, NetworkItem> netFaces = new HashMap<String, NetworkItem>();
        try {
            Enumeration<NetworkInterface> network = NetworkInterface.getNetworkInterfaces();
            while (network.hasMoreElements()) {
                NetworkItem netItem = new NetworkItem();
                NetworkInterface face = network.nextElement();
                byte[] data = face.getHardwareAddress();
                try {
                    if (data != null && data.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : data)
                            sb.append(Strings.toHex(b, 2));
                        netItem.setMac(sb.toString().toUpperCase());
                    }
                }
                catch (Throwable e) {}
                List<InterfaceAddress> addrs = face.getInterfaceAddresses();
                if (addrs != null && !addrs.isEmpty()) {
                    for (InterfaceAddress interfaceAddress : addrs) {
                        String ip = interfaceAddress.getAddress().getHostAddress();
                        if (ip == null || ip.length() == 0)
                            continue;
                        if (ip.contains("."))
                            netItem.setIpv4(ip);
                        else
                            netItem.setIpv6(ip);
                    }
                }
                netItem.setMtu(face.getMTU());
                
                if (netItem.getIpv4() == null && netItem.getMac() == null && netItem.getMtu() < 1)
                	continue;
                netFaces.put(face.getName(), netItem);
            }
        }
        catch (Throwable e) {}
        return netFaces;
    }

    /**
     * @return 返回当前第一个可用的IP地址
     */
    public static String ipv4() {
    	for (NetworkItem item : networkItems().values()) {
			if (!Strings.isBlank(item.getIpv4()) && !"127.0.0.1".equals(item.getIpv4()))
				return item.getIpv4();
		}
    	return null;
    }

    /**
     * @param nt
     * @return 返回对应类型的IP地址
     */
    public static String ipv4(NetworkType nt) {
        Map<String, NetworkItem> netFaces = networkItems();
        if (netFaces.isEmpty()) {
            return null;
        }
        NetworkItem networkItem = getNetworkByType(netFaces, ntMap.get(nt));
        return networkItem == null ? null : networkItem.getIpv4();
    }

    /**
     * @return 返回当前第一个可用的MAC地址
     */
    public static String mac() {
        NetworkItem networkItem = firstNetwokrItem();
        if (networkItem == null)
            return null;
        return networkItem.getMac();
    }

    /**
     * @param nt
     * @return 返回对应类型的MAC地址
     */
    public static String mac(NetworkType nt) {
        Map<String, NetworkItem> netFaces = networkItems();
        if (netFaces.isEmpty()) {
            return null;
        }
        NetworkItem networkItem = getNetworkByType(netFaces, ntMap.get(nt));
        return networkItem == null ? null : networkItem.getMac();
    }

    private static NetworkItem firstNetwokrItem() {
        Map<String, NetworkItem> netFaces = networkItems();
        if (netFaces.isEmpty()) {
            return null;
        }
        // 依次尝试
        NetworkItem re = null;
        re = getNetworkByType(netFaces, ntMap.get(NetworkType.LAN));
        if (re == null) {
            re = getNetworkByType(netFaces, ntMap.get(NetworkType.WIFI));
        }
        if (re == null) {
            re = getNetworkByType(netFaces, ntMap.get(NetworkType.ThreeG));
        }
        if (re == null) {
            re = getNetworkByType(netFaces, ntMap.get(NetworkType.VPN));
        }
        if (re == null) {
        	for (Entry<String, NetworkItem> en : netFaces.entrySet()) {
				if (Strings.isBlank(en.getValue().getIpv4()))
					continue;
				if (Strings.isBlank(en.getValue().getMac()))
					continue;
				return en.getValue();
			}
        }
        return re;
    }

    private static NetworkItem getNetworkByType(Map<String, NetworkItem> netFaces, String nt) {
        String[] nss = Strings.splitIgnoreBlank(nt, ",");
        for (String ns : nss) {
            for (int i = 0; i < 10; i++) {
                if (netFaces.containsKey(ns + i))
                    return netFaces.get(ns + i);
            }
        }
        return null;
    }
}
