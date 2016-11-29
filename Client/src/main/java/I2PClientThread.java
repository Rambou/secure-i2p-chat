import net.i2p.I2PException;
import net.i2p.client.streaming.I2PSocket;
import net.i2p.client.streaming.I2PSocketManager;
import net.i2p.client.streaming.I2PSocketManagerFactory;
import net.i2p.data.DataFormatException;
import net.i2p.data.Destination;

import java.io.*;
import java.net.ConnectException;
import java.net.NoRouteToHostException;

class I2PClientThread implements Runnable {

    private String destinationString;
    private I2PSocket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public I2PClientThread(String destinationString) {
        this.destinationString = destinationString;
    }

    @Override
    public void run() {
        I2PSocketManager manager = I2PSocketManagerFactory.createManager();

        Destination destination;
        try {
            destination = new Destination(destinationString);
        } catch (DataFormatException ex) {
            System.out.println("Destination string incorrectly formatted.");
            return;
        }
        try {
            socket = manager.connect(destination);
        } catch (I2PException ex) {
            System.out.println("General I2P exception occurred!");
            return;
        } catch (ConnectException ex) {
            System.out.println("Failed to connect!");
            return;
        } catch (NoRouteToHostException ex) {
            System.out.println("Couldn't find host!");
            return;
        } catch (InterruptedIOException ex) {
            System.out.println("Sending/receiving was interrupted!");
            return;
        }
        while (true) {
            try {
                // Write to server - Read from server
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String s = null;
                while ((s = reader.readLine()) != null) {
                    System.out.println("Received from server: " + s);
                }
            } catch (IOException ex) {
                System.out.println("Error occurred while sending/receiving!");
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        writer.write(message);
        //Flush to make sure everything got sent
        writer.flush();
    }
}
