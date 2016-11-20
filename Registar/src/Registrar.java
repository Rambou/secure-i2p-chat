import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Registrar implements Runnable {
    // λίστα που κρατά όλους τους συνδεδεμένους clients στον server κλειδί
    // το username και object το InOut (in, out, socket)
    private final HashMap<String, InOut> clients = new HashMap<String, InOut>();
    private ServerSocket socket;

    @SuppressWarnings("InfiniteLoopStatement")
    Registrar(int port) throws IOException {
        // initialize socket server
        socket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
        System.out.println("Waiting for client on port " + socket.getLocalPort() + " at " + socket.getInetAddress().toString());

        // Thread που διαχειρίζεται τους clients
        new Thread(() -> {
            // true για να ακούει πάντα
            while (true) {
                try {
                    Socket sock = socket.accept();
                    System.out.println("Client just connected to " + sock.getRemoteSocketAddress());
                    // πρόσθεσε το socket σε μια λίστα που θα κρατά τις συνδέσεις
                    // πρόσθεσε το input Steam που θα ακούει για μυνήματα από τον client
                    // είναι synchronized επειδή το χρησημοποιούμε και παρακάτω
                    synchronized (clients) {
                        InOut stream = new InOut(sock);
                        clients.put(sock.getRemoteSocketAddress().toString(), stream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            // Κράτα το πάντα ζωντανό
            while (true) {
                // επανάληψη για κάθε Client input stream
                synchronized (clients) {
                    Set set = clients.entrySet();
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry client = (Map.Entry) iterator.next();
                        DataInputStream e = ((InOut) client.getValue()).getIn();
                        if (e.available() <= 0) {
                            continue;
                        }

                        String message = e.readUTF();
                        if (message.contains("username")) {
                            // θέτει ως κλειδί το username του χρήστη
                            // και αντικαθιστά το προσωρινό προηγούμενο που ήταν η ip του.
                            String username = message.split(":")[1];
                            clients.put(username,clients.remove((String) client.getKey()));
                            System.out.println(username + " registered.");

                            // στείλε στον client ότι συνδέθηκε
                            Socket sock = ((InOut) client.getValue()).getSock();
                            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                            out.writeUTF("Thank you for connecting to " + sock.getLocalSocketAddress());
                        }
                        if(message.contains("getClients")){
                            // στείλε στον client την λίστα με τους συνδεμένους χρήστες
                            Socket sock = ((InOut) client.getValue()).getSock();
                            ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
                            out.writeObject(clients);
                            out.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
