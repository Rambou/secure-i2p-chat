import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;

public class Main {
    private static InetAddress host;
    private static Integer port;
    private static String i2pUrl;

    public static void main(String[] args) throws IOException {
        I2PServer i2pServe = new I2PServer();
        //παιρνει την I2P διευθυνση από τον I2pserver
        i2pUrl = i2pServe.getSessionDest();
        String username = "rambou";
        Client newClient = new Client(username, i2pUrl);

        // Διαβάζει τις παραμέτρους για την σύνδεση στον registrar
        if (args.length == 0) {
            host = InetAddress.getByName("0.0.0.0");
            port = 3000;
        }
        if (args.length == 1) {
            host = InetAddress.getByName(String.valueOf(args[0]));
            port = 3000;
        } else if (args.length == 2) {
            host = InetAddress.getByName(String.valueOf(args[0]));
            port = Integer.valueOf(args[1]);
        }

        // Σύνδεση στον registrar
        // και εγγραφή
        try {
            // Eδραιώνει την σύνδεση με τον registrar
            System.out.println("Connecting to registrar" + host + " on port " + port);
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket registrar = (SSLSocket) sslsocketfactory.createSocket(host, port);
            System.out.println("Just connected to " + registrar.getRemoteSocketAddress());

            // στέλνει στον registrar ένα αντικείμενο που περιέχει τις πληροφορίες του
            OutputStream outToServer = registrar.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToServer);
            out.writeObject(newClient);

            // περιμένει απάντηση από τον server για την εγραφή του
            InputStream inFromServer = registrar.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server says " + (String) in.readUTF());

            // περιμένει απάντηση από τον server της λίστας των εγγεγραμμένων χρηστών
            ObjectInputStream ino = new ObjectInputStream(inFromServer);
            HashMap<String, String> regClients = (HashMap<String, String>) ino.readObject();

            registrar.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
