package org.nutz.lang.hardware;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.lang.Lang;
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
        Map<String, NetworkItem> netFaces = new LinkedHashMap<String, NetworkItem>();
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
                        if (netItem.getMac().startsWith("000000000"))
                            continue;
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
                netItem.setDisplay(face.getDisplayName());
                
                if (netItem.getIpv4() == null && netItem.getMac() == null && netItem.getMtu() < 1 && !face.getName().startsWith("eth"))
                	continue;
                netFaces.put(face.getName(), netItem);
            }
        }
        catch (Throwable e) {}
        if (Lang.isWin() && netFaces.size() > 0) {
            for (Entry<String, NetworkItem> en : netFaces.entrySet()) {
                NetworkItem item = en.getValue();
                if (item != null && ipOk(item.getIpv4()) && item.getIpv4().startsWith("10.")) {
                    netFaces.put("tun0", item);
                    break;
                }
            }
        }
        return netFaces;
    }

    /**
     * @return 返回当前第一个可用的IP地址
     */
    public static String ipv4() {
        Map<String, NetworkItem> items = networkItems();
        // 先遍历一次eth开头的
        for (int i = 0; i < 10; i++) {
            NetworkItem item = items.get("eth"+i);
            if (item != null) {
                String ip = item.getIpv4();
                if (ipOk(ip))
                    return ip;
            }
        }
    	for (NetworkItem item : items.values()) {
    	    String ip = item.getIpv4();
			if (ipOk(ip))
				return ip;
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
        List<NetworkItem> list = getNetworkByTypes(netFaces, ntMap.get(nt));
        for (NetworkItem item : list) {
            if (!Strings.isBlank(item.getIpv4()))
                return item.getIpv4();
        }
        return null;
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
        List<NetworkItem> list = getNetworkByTypes(netFaces, ntMap.get(nt));
        for (NetworkItem item : list) {
            if (!Strings.isBlank(item.getMac()))
                return item.getMac();
        }
        return null;
    }

    private static NetworkItem firstNetwokrItem() {
        Map<String, NetworkItem> netFaces = networkItems();
        if (netFaces.isEmpty()) {
            return null;
        }
        // 依次尝试
        List<NetworkItem> re = null;
        re = getNetworkByTypes(netFaces, ntMap.get(NetworkType.LAN));
        if (re.isEmpty()) {
            re = getNetworkByTypes(netFaces, ntMap.get(NetworkType.WIFI));
        }
        if (re.isEmpty()) {
            re = getNetworkByTypes(netFaces, ntMap.get(NetworkType.ThreeG));
        }
        if (re.isEmpty()) {
            re = getNetworkByTypes(netFaces, ntMap.get(NetworkType.VPN));
        }
        if (re.isEmpty()) {
        	for (Entry<String, NetworkItem> en : netFaces.entrySet()) {
				if (Strings.isBlank(en.getValue().getIpv4()))
					continue;
				if (Strings.isBlank(en.getValue().getMac()))
					continue;
				return en.getValue();
			}
        }
        return re.get(0);
    }

    private static List<NetworkItem> getNetworkByTypes(Map<String, NetworkItem> netFaces, String nt) {
        List<NetworkItem> list = new ArrayList<NetworkItem>();
        String[] nss = Strings.splitIgnoreBlank(nt, ",");
        for (String ns : nss) {
            for (int i = 0; i < 10; i++) {
                if (netFaces.containsKey(ns + i))
                    list.add(netFaces.get(ns + i));
            }
        }
        return list;
    }
    
    public static boolean ipOk(String ip) {
        return (!Strings.isBlank(ip) && !ip.startsWith("127.0") && !ip.startsWith("169."));
    }
}
