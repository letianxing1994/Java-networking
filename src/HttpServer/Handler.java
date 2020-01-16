package HttpServer;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface Handler {
    public void handle(SelectionKey key) throws IOException;
}
