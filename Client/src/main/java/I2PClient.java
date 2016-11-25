import net.i2p.util.I2PThread;

public class I2PClient {
    private String destinationString;

    public I2PClient(String destinationString) {
        this.destinationString = destinationString;

        //Create socket to handle clients
        I2PThread t = new I2PThread(new I2PClientThread(destinationString));
        t.setName("serverhandler1");
        t.setDaemon(false);
        t.start();
    }
}
