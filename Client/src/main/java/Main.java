import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.util.Map;

public class Main {
    private static InetAddress host;
    private static Integer port;
    private static String i2pUrl;
    private static String username;
    private static Map<String, String> regClients;
    private static Online online;

    public static void main(String[] args) throws IOException {


        host = InetAddress.getByName("0.0.0.0");
        port = 3000;

        // Διαβάζει τις παραμέτρους για την σύνδεση στον registrar
        for (int i = 0; i < args.length; i++) {
            switch (args[i].split("=")[0]) {
                case "host":
                    host = InetAddress.getByName(String.valueOf(args[i].split("=")[1]));
                    break;
                case "port":
                    port = Integer.valueOf(args[i].split("=")[1]);
                    break;
                case "username":
                    username = String.valueOf(args[i].split("=")[1]);
                    break;
                default:
                    break;
            }
        }


        I2PServer i2pServe = new I2PServer();
        //παιρνει την I2P διευθυνση από τον I2pserver
        i2pUrl = i2pServe.getSessionDest();
        //i2pUrl = "good";
        Client newClient = new Client(username, i2pUrl);

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
            ObjectInputStream in = new ObjectInputStream(inFromServer);
            System.out.println("Server says, " + (String) in.readObject());

            startGui();
            // περιμένει απάντηση από τον server της λίστας των εγγεγραμμένων χρηστών
            while (true) {
                Thread.sleep(1000);
                regClients = (Map<String, String>) in.readObject();
                System.out.println("Clients are, " + regClients.toString());
                online.updateUsers(regClients);

            }
            //registrar.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startGui() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                (online = new Online(regClients)).setVisible(true);
            }
        });
    }

}
