package com.company;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Socket socket = new Socket();
        try {
            InetAddress addr1 = InetAddress.getLocalHost();
            InetAddress addr2 = InetAddress.getByName("www.baidu.com");
            InetAddress addr3 = InetAddress.getByName("104.193.88.77");
            Server server = new Server(9999);
            client c = new client(addr1, 9999);
            System.out.println(server.getSocket());
            System.out.println(c.getSocket());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

class Server{
    private ServerSocket ss;
    public Server(int port) {
        try {
            ss = new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Socket getSocket(){
        Socket s = null;
        try {
            s = ss.accept();
            return s;
        }catch(Exception e){
            return s;
        }
    }
}

class client{
    private Socket s;
    public client(InetAddress addr, int port){
        try {
            s = new Socket(addr, port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Socket getSocket(){
        return s;
    }
}
