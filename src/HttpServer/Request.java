package HttpServer;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URI;

public class Request {
    static class Action {
        private String name;

        private Action(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        static Action GET = new Action("GET");
        static Action PUT = new Action("PUT");
        static Action POST = new Action("POST");
        static Action HEAD = new Action("HEAD");

        public static Action parse(String s) {
            if (s.equals("GET"))
                return GET;
            if (s.equals("PUT"))
                return PUT;
            if (s.equals("POST"))
                return POST;
            if (s.equals("HEAD"))
                return HEAD;
            throw new IllegalArgumentException(s);
        }
    }

    private Action action;
    private String version;
    private URI uri;

    public Action action() {return action;}
    public String version() {return version;}
    public URI uri() {return uri;}

    private Request(Action a, String v, URI u){
        action = a;
        version = v;
        uri = u;
    }

    public String toString(){
        return (action+""+version+""+uri);
    }

    private static Charset requestCharset = Charset.forName("GBK");

    public static boolean isComplete(ByteBuffer bb){
        ByteBuffer temp = bb.asReadOnlyBuffer();
        temp.flip();
        String data = requestCharset.decode(temp).toString();
        if(data.indexOf("\r\n\r\n")!=-1){
            return true;
        }
        return false;
    }

    private static ByteBuffer deleteContent(ByteBuffer bb){
        ByteBuffer temp = bb.asReadOnlyBuffer();
        String data = requestCharset.decode(temp).toString();
        if(data.indexOf("\r\n\r\n")!=-1){
            data = data.substring(0,data.indexOf("\r\n\r\n")+4);
            return requestCharset.encode(data);
        }
        return bb;
    }

    private static Pattern requestPattern
            = Pattern.compile("\\A([A-Z]+)+([^ ]+)+HTTP/([0-9\\.]+)$"
                    +".*^Host:([^ ]+)$.*\r\n\r\n\\z",
            Pattern.MULTILINE|Pattern.DOTALL);

    public static Request parse(ByteBuffer bb) throws MalformedRequestException{
        bb = deleteContent(bb);
        CharBuffer cb = requestCharset.decode(bb);
        Matcher m = requestPattern.matcher(cb);

        if(!m.matches())
            throw new MalformedRequestException();
        Action a;
        try{
            a = Action.parse(m.group(1));
        }catch(IllegalArgumentException x){
            throw new MalformedRequestException();
        }
        URI u;
        try{
            u = new URI("http://"
            +m.group(4)
            +m.group(2));
        }catch(URISyntaxException x){
            throw new MalformedRequestException();
        }

        return new Request(a,m.group(3),u);
    }
}
