import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Registrar {
    // λίστα που κρατά όλους τους συνδεδεμένους clients στον server κλειδί
    // το username και object το InOut (in, out, ServerSocket)
    private final ArrayList<Connection> clients = new ArrayList<>();
    private final ConcurrentHashMap<String, String> regClients = new ConcurrentHashMap<>();
    private SSLServerSocket ServerSocket;
    private Integer count = 0;

    @SuppressWarnings("InfiniteLoopStatement")
    Registrar(int port) throws IOException {
        // initialize ServerSocket server
        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
        // Ενεργοποίηση του TLS 1.2
        ServerSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
        System.out.println("Waiting for client on port " + ServerSocket.getLocalPort() + " at " + ServerSocket.getInetAddress().toString());

        // Δημιουργία ενός Event listener που θα διαχειρίζεται τους χρήστες που αποσυνδέονται
        // όταν κάποιος αποσυνδέεται ο συγκεκριμένος handler θα αναλαμβάνει αν στείλει την καινούργια λίστα σε όλους
        AlertHandler alert = new AlertHandler(this);

        // Thread που διαχειρίζεται τους clients
        new Thread(() -> {
            // true για να ακούει πάντα
            while (true) {
                try {
                    SSLSocket sock = (SSLSocket) ServerSocket.accept();
                    System.out.println("Client just connected to " + sock.getRemoteSocketAddress());
                    // Αρχικοποίηση του Διαχεριστή χρηστών με  το socket του client και
                    // το regClients, μια λίστα που θα κρατά τους συνδεδεμένους χρήστες
                    ClientHandler ch = new ClientHandler(sock, clients, ++count);
                    // Προσθήκη handler ως listener
                    ch.addListener(alert);
                    // Εκκίνηση του νήματος διαχείρισης χρηστών
                    Thread thread = new Thread(ch);
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ArrayList<Connection> getClients() {
        return clients;
    }

    public ConcurrentHashMap<String, String> getRegClients() {
        return regClients;
    }

}
