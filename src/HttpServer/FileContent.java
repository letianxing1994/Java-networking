package HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.net.URI;

public class FileContent implements Content {
    private static File ROOT = new File("root");
    private File file;

    public FileContent(URI uri){
        file = new File(ROOT,
                uri.getPath().replace('/',File.separatorChar));
    }

    private String type = null;

    public String type(){
        if(type!=null) return type;
        String nm = file.getName();
        if(nm.endsWith(".html")||nm.endsWith(".htm"))
            type = "text/html;charset=iso-8859-1";
        else if((nm.indexOf('.')<0)||nm.endsWith(".txt"))
            type = "text/plain;charset=iso-8859-1";
        else
            type = "application/octet-stream";
        return type;
    }

    private FileChannel fileChannel = null;
    private long length = -1;
    private long position = -1;

    public long length(){
        return length;
    }

    public void prepare() throws IOException {
        if(fileChannel==null)
            fileChannel = new RandomAccessFile(file, "r").getChannel();
        length = fileChannel.size();
        position = 0;
    }

    public boolean send(ChannelIO channelIO) throws IOException{
        if(fileChannel==null)
            throw new IllegalStateException();
        if(position<0)
            throw new IllegalStateException();

        if(position>=length){
            return false;
        }

        position += channelIO.transferTo(fileChannel,position,length-position);
        return (position<length);
    }

    public void release() throws IOException{
        if(fileChannel!=null){
            fileChannel.close();
            fileChannel=null;
        }
    }
}
