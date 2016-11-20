import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class Main {
    private static InetAddress host;
    private static Integer port;
    private static String i2pUrl;

    public static void main(String[] args) throws IOException {
        I2PServer i2pServe = new I2PServer();
        //παιρνει την I2P διευθυνση από τον I2pserver
        i2pUrl = i2pServe.getSessionDest();

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
            System.out.println("Connecting to registrar" + host + " on port " + port);
            Socket registrar = new Socket(host, port);
            System.out.println("Just connected to " + registrar.getRemoteSocketAddress());

            OutputStream outToServer = registrar.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToServer);
            out.writeObject(new Message("rambou", i2pUrl));

            InputStream inFromServer = registrar.getInputStream();
            ObjectInputStream in = new ObjectInputStream(inFromServer);
            System.out.println("Server says " + (String) in.readObject());

            ObjectInputStream ino = new ObjectInputStream(inFromServer);
            HashMap<String, InOut> clients = (HashMap<String, InOut>)ino.readObject();


            registrar.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //new Main().init(host, port);
    }

}
