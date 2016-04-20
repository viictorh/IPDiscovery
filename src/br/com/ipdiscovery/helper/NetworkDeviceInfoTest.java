package br.com.ipdiscovery.helper;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jpcap.capture.PacketCapture;


public class NetworkDeviceInfoTest {

    public static void main (String[] args) throws Exception {

        List<NetworkDeviceInfo> infos = new ArrayList<NetworkDeviceInfo>();

        // Info can be queried from jpcap device string.
        for (String jpcapDevice : PacketCapture.lookupDevices())
            infos.add(new NetworkDeviceInfo(jpcapDevice));

        // Info can be displayed.
        for (NetworkDeviceInfo info : infos) {
            System.out.println(info.getJpcapDeviceName() + ":");
            System.out.println("  Description:   " + info.getDriverName());
            System.out.println("  Vendor:        " + info.getDriverVendor());
            System.out.println("  Address:       " + info.getInterfaceAddress());
            System.out.println("  Subnet Mask:   " + info.getInterfaceSubnetMask());
            System.out.println("  jpcap Display: " + info.getJpcapDisplayName());
            System.out.println("  GUID:          " + info.getGuid());
        }

        // Device names from NetworkDeviceInfo can be passed directly to jpcap:
        NetworkDeviceInfo selected = infos.get(0);
        PacketCapture capture = new PacketCapture();
        capture.open(selected.getJpcapDeviceName(), true);

    }

}