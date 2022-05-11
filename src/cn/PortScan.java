package cn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * Create by Intellij IDEA
 * User: wanghao
 * Date: 2022/5/11
 */
public class PortScan {
    public static void main(String[] args){
        ArrayList<Integer> portIntegerArrayList = new ArrayList<>();
        Socket socket=null;
        //遍历端口
        for(int port=1; port<65535; port++){
            System.out.println("扫描端口"+port+"...");
            //封装地址和端口
            InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.42.77", port);
            //创建套接字
            socket = new Socket();
            //通过套接字进行连接，并设置超时时间
            try {
                socket.connect(inetSocketAddress,1);//超时时间100ms
                socket.close();
                portIntegerArrayList.add(port);
            } catch (IOException e) {

            }
        }
        for(int port:portIntegerArrayList){
            System.out.println("端口" + port + " ：开放");
        }
    }
}
