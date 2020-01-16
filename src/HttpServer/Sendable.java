package HttpServer;

import java.io.IOException;

public interface Sendable {
    public void prepare() throws IOException;

    public boolean send(ChannelIO cio) throws IOException;

    public void release() throws IOException;
}
