package HttpServer;

public interface Content extends Sendable{
    String type();

    long length();
}
