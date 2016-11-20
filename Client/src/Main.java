import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class Main {
    private static InetAddress host;
    private static Integer port;

    public static void main(String[] args) throws IOException {
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
            DataOutputStream out = new DataOutputStream(outToServer);
            // send username to registrar
            out.writeUTF("username:rambou");
            InputStream inFromServer = registrar.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            System.out.println("Server says " + in.readUTF());

            ObjectInputStream ino = new ObjectInputStream(inFromServer);
            HashMap<String, InOut> clients = (HashMap<String, InOut>)ino.readObject();
            System.out.print(clients.get("rambou").getSock());
            registrar.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //new Main().init(host, port);
    }

}
