package NonBlockingOneMainThreadServer;

import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private int port = 8000;
    private Charset charset = Charset.forName("GBK");
    public EchoServer()throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
    }

    public String decode(ByteBuffer buffer){
        CharBuffer charBuffer = charset.decode(buffer);
        return charBuffer.toString();
    }

    public ByteBuffer encode(String str){
        return charset.encode(str);
    }

    public void receive(SelectionKey key)throws IOException{
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer readBuff = ByteBuffer.allocate(32);
        socketChannel.read(readBuff);
        readBuff.flip();

        buffer.limit(buffer.capacity());
        buffer.put(readBuff);
    }

    public void send(SelectionKey key)throws IOException{
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        SocketChannel socketChannel = (SocketChannel)key.channel();
        buffer.flip();
        String data = decode(buffer);
        if(data.indexOf("\r\n")==-1) return;
        String outputData = data.substring(0,data.indexOf("\n")+1);
        System.out.print(outputData);
        ByteBuffer outputBuffer = encode("echo:"+outputData);
        while(outputBuffer.hasRemaining())
            socketChannel.write(outputBuffer);

        ByteBuffer temp = encode(outputData);
        buffer.position(temp.limit());
        buffer.compact();
        if(outputData.equals("bye\r\n")){
            key.cancel();
            socketChannel.close();
            System.out.println("close connection with client");
        }
    }

    public void service() throws IOException{
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while(selector.select()>0){     //first while loop
            Set readyKeys = selector.selectedKeys(); //selected_keys collection
            Iterator it = readyKeys.iterator();
            while(it.hasNext()){  //second loop
                SelectionKey key = null;
                try{
                    key = (SelectionKey)it.next();
                    it.remove();
                    if(key.isAcceptable()){
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = (SocketChannel)ssc.accept();
                        System.out.println("client connection received, it is from:"+
                                socketChannel.socket().getInetAddress()+":"+socketChannel.socket().getPort());
                        socketChannel.configureBlocking(false);
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE,
                                buffer);
                    }
                    if(key.isReadable()){
                        receive(key);
                    }
                    if(key.isWritable()){
                        send(key);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                    try{
                        if(key!=null){
                            key.cancel();
                            key.channel().close();
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args)throws Exception{
        EchoServer server = new EchoServer();
        server.service();
    }
}
