package HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class HttpServer {
    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;
    private int port = 80;
    private Charset charset = Charset.forName("GBK");

    public HttpServer()throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
    }

    public void service() throws IOException{
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new AcceptHandler());
        while(true){
            int n = selector.select();

            if(n==0) continue;
            Set readyKeys = selector.selectedKeys();
            Iterator it = readyKeys.iterator();
            while(it.hasNext()){
                SelectionKey key = null;
                try{
                    key = (SelectionKey) it.next();
                    it.remove();
                    final Handler handler = (Handler)key.attachment();
                    handler.handle(key);
                }catch(IOException e){
                    e.printStackTrace();
                    try{
                        if(key!=null){
                            key.cancel();
                            key.channel().close();
                        }
                    }catch(Exception ex){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args)throws Exception{
        final HttpServer server = new HttpServer();
        server.service();
    }
}
