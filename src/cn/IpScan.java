package cn;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by Intellij IDEA
 * User: wanghao
 * Date: 2022/5/9
 */
public class IpScan {
    static class InetAddressObject{
        String name;
        String ip;
        String displayName;
        String cpName;
        boolean isUse;
        public InetAddressObject(String name, String ip, String displayName,String cpName,boolean isUse) {
            this.name = name;
            this.ip = ip;
            this.displayName = displayName;
            this.cpName = cpName;
            this.isUse = isUse;
        }
    }
    private List<InetAddressObject> getInetAddress() throws SocketException {
        ArrayList<InetAddressObject> inetAddressList = new ArrayList<>();

        //思路：获取当前机器所有网络接口，然后筛选出表示局域网的网络接口，就可以获取到机器在局域网中的ip地址了
        //获取所有网络接口（一个网络接口通常由一个ip地址表示）
        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        //遍历所有网络接口
        while (allNetInterfaces.hasMoreElements()) {
            //网络接口，通常对应一个ip地址
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            //过滤回环地址、是否为虚拟接口、是否已启动
            if(netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()){
                continue;
            }
            Enumeration<InetAddress> inetAddress = netInterface.getInetAddresses();
            while (inetAddress.hasMoreElements()) {
                InetAddress ip = inetAddress.nextElement();

                if (ip != null && ip instanceof Inet4Address) {
                    System.out.println("网卡接口名称："+netInterface.getName()+
                            "，网卡接口地址："+ip.getHostAddress()+
                            //网络接口的显示名称（描述网络设备的人类可读字符串）
                            "，网卡接口显示名称："+netInterface.getDisplayName());
                    InetAddressObject inetAddressObject = new InetAddressObject(netInterface.getName(), ip.getHostAddress(), netInterface.getDisplayName(),null,false);
                    inetAddressList.add(inetAddressObject);
                }
            }
        }
        return inetAddressList;
    }
    public static void main(String[] args) {
        try {
            List<InetAddressObject> inetAddressObjectList = new IpScan().getInetAddress().stream().filter(o->!o.displayName.contains("VMware")).collect(Collectors.toList());
            //本机局域网地址
            String hostAddress = inetAddressObjectList.get(0).ip;
            //网段
            String networkSegment = hostAddress.substring(0, hostAddress.lastIndexOf("."));
            String ip;
            InetAddress inetAddress;
            System.out.println("开始扫描网段"+networkSegment+"...");
            ArrayList<InetAddressObject> inetAddressList = new ArrayList<>();
            //遍历IP地址
            for (int i = 1; i < 255; i++) {
                ip = networkSegment+ "." + i;
//                int waitFor = Runtime.getRuntime().exec("ping -c 1 " + ip).waitFor();
//                System.out.println(waitFor);
                System.out.println("正在扫描"+ip+"...");
                //根据ip字符串获取ip对象
                inetAddress = InetAddress.getByName(ip);
                //判断是否能够访问到，可以访问到返回“主机名/IP”，访问不到返回IP
                String inetAddressHostName = inetAddress.getHostName();
                if(ip.equals(inetAddressHostName)){
                    //连接失败
                }else{
                    //连接成功，获取连接状态设置超时时间
                    boolean status = InetAddress.getByName(inetAddressHostName).isReachable(1000);
                    InetAddressObject inetAddressObject = new InetAddressObject(null, ip, null,inetAddress.getHostName(),status);
                    inetAddressList.add(inetAddressObject);
                }
            }
            for(InetAddressObject inetAddressObject:inetAddressList){
                System.out.println("IP为：" + inetAddressObject.ip + "\t主机名为：" + inetAddressObject.cpName + "\t与本机网络是否通畅: " + (inetAddressObject.isUse ? "通畅" : "不通畅"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


