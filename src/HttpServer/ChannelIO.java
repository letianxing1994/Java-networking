package HttpServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ChannelIO {
    protected SocketChannel socketChannel;
    protected ByteBuffer requestBuffer;
    private static int requestBufferSize = 4096;

    public ChannelIO(SocketChannel socketChannel,boolean blocking)
            throws IOException {
        this.socketChannel = socketChannel;
        socketChannel.configureBlocking(blocking);
        requestBuffer = ByteBuffer.allocate(requestBufferSize);
    }

    public SocketChannel getSocketChannel(){
        return socketChannel;
    }

    protected void resizeRequestBuffer(int remaining){
        if(requestBuffer.remaining()<remaining){
            ByteBuffer bb = ByteBuffer.allocate(requestBuffer.capacity()*2);
            requestBuffer.flip();
            bb.put(requestBuffer);
            requestBuffer = bb;
        }
    }

    public int read() throws IOException{
        resizeRequestBuffer(requestBufferSize/20);
        return socketChannel.read(requestBuffer);
    }

    public ByteBuffer getReadBuf(){
        return requestBuffer;
    }

    public int write(ByteBuffer src) throws IOException{
        return socketChannel.write(src);
    }

    public long transferTo(FileChannel fc, long pos, long len) throws IOException{
        return fc.transferTo(pos, len, socketChannel);
    }

    public void close() throws IOException{
        socketChannel.close();
    }
}
