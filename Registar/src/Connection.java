import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket sock;
    private Client client;

    public Connection(ObjectInputStream in, ObjectOutputStream out, Socket sock) throws IOException {
        this.sock = sock;
        this.in = in;
        this.out = out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public Socket getSock() {
        return sock;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
