import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class InOut implements java.io.Serializable{
    private DataInputStream in;
    private DataOutputStream out;
    private Socket sock;

    public InOut(Socket sock) throws IOException {
        this.sock = sock;
        this.in = new DataInputStream(sock.getInputStream());
        this.out = new DataOutputStream(sock.getOutputStream());
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public Socket getSock() {
        return sock;
    }
}
