package org.nutz.lang.hardware;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Strings;

public class Networks {

	public static Map<String, NetworkItem> macs() {
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
                netFaces.put(face.getName(), netItem);
            }
        }
        catch (Throwable e) {}
        return netFaces;
    }
}
