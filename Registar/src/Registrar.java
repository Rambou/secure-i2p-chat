import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// Tutorial on how to run with SSL
// http://stilius.net/java/java_ssl.php

public class Registrar implements Runnable {
    // λίστα που κρατά όλους τους συνδεδεμένους clients στον server κλειδί
    // το username και object το InOut (in, out, ServerSocket)
    private final HashMap<String, InOut> clients = new HashMap<String, InOut>();
    private SSLServerSocket ServerSocket;


    @SuppressWarnings("InfiniteLoopStatement")
    Registrar(int port) throws IOException {
        // initialize ServerSocket server
        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
        // Ενεργοποίηση του TLS 1.2
        ServerSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
        System.out.println("Waiting for client on port " + ServerSocket.getLocalPort() + " at " + ServerSocket.getInetAddress().toString());

        // Thread που διαχειρίζεται τους clients
        new Thread(() -> {
            // true για να ακούει πάντα
            while (true) {
                try {
                    SSLSocket sock = (SSLSocket) ServerSocket.accept();
                    System.out.println("Client just connected to " + sock.getRemoteSocketAddress());
                    // πρόσθεσε το ServerSocket σε μια λίστα που θα κρατά τις συνδέσεις
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
