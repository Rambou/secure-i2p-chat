import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static InetAddress host;
    private static Integer port;
    private Selector selector;
    private Charset charset = Charset.forName("UTF-8");
    private SocketChannel sc = null;

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

    public void init(InetAddress ip, int port) throws IOException {
        selector = Selector.open();
        InetSocketAddress isa = new InetSocketAddress(ip, port);
        sc = SocketChannel.open(isa);
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        new ClientThread().start();
        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()) {
            sc.write(charset.encode(scan.nextLine()));
        }
    }

    private class ClientThread extends Thread {
        public void run() {
            try {
                while (selector.select() > 0) {
                    for (SelectionKey sk : selector.selectedKeys()) {
                        selector.selectedKeys().remove(sk);
                        if (sk.isReadable()) {
                            SocketChannel sc = (SocketChannel) sk.channel();
                            ByteBuffer buff = ByteBuffer.allocate(1024);
                            String content = "";
                            while (sc.read(buff) > 0) {
                                sc.read(buff);
                                buff.flip();
                                content += charset.decode(buff);
                                buff.clear();
                            }
                            System.out.println("chat info: " + content);
                            sk.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
