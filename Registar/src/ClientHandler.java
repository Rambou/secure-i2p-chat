import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by rambou on 22/11/2016.
 */
public class ClientHandler implements Runnable {

    private Socket sock;
    private Integer ID;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Client client;
    private ArrayList<Connection> connections;
    private AlertListener listener = null;
    private Connection conn;

    public ClientHandler(Socket socket, ArrayList<Connection> connections, int id) throws IOException {
        this.sock = socket;
        this.ID = id;
        // αρχικοποίηση των streams
        this.in = new ObjectInputStream(sock.getInputStream());
        this.out = new ObjectOutputStream(sock.getOutputStream());
        this.connections = connections;
        this.conn = new Connection(in, out, sock);
        connections.add(conn);
    }

    public void addListener(AlertListener listener) {
        this.listener = listener;
    }

    private void informListener() {
        if (listener != null) {
            // αποστολή στον listener το username αυτού του χρήστη
            listener.onUserDisconnected(client.getUsername(), sock.getRemoteSocketAddress().toString(), conn);
        }
    }

    private void userConnected() {
        if (listener != null) {
            // αποστολή στον listener το username αυτού του χρήστη
            listener.onUserConnected(conn);
        }
    }

    @Override
    public void run() {
        // Κράτα το πάντα ζωντανό
        while (true) {
            try {
                // παύση του thread για μη άσκοπη χρήση CPU
                Thread.sleep(1000);

                // διαβάζουμε το αντικέιμενο
                Object obj = in.readObject();

                // Τι τύπου είναι το αντικείμενο
                if (obj instanceof Client) {
                    // προσθήκη του client στην λίστα
                    client = (Client) obj;
                    conn.setClient(client);
                    System.out.println(client.getUsername() + " registered.");

                    // στείλε στον client ότι συνδέθηκε
                    out.writeObject("Thank you for registering to " + sock.getLocalSocketAddress());
                    out.flush();

                    // στείλε στον client την λίστα με τους συνδεμένους χρήστες
                    // σε όλους τους χρήστες
                    userConnected();
                    //sendClientList();
                } else if (obj instanceof String) {
                    String message = (String) obj;
                    if (message.contains("GET_CLIENT_LIST")) {
                        // στείλε στον client την λίστα με τους συνδεμένους χρήστες
                        sendClientList();
                    }
                }

            } catch (EOFException | SocketException e) {
                connections.remove(conn);
                // αποστολή της καινούργιας λίστας στους υπόλοιπους client
                // ο χρήστης αποσυνδέθηκε
                informListener();

                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendClientList() throws IOException {
        //out.writeObject(registered);
        out.flush();
    }
}