import net.i2p.util.I2PThread;

public class I2PClient {
    private String destinationString;
    private I2PClientThread cthread;

    public I2PClient(String destinationString) {
        this.destinationString = destinationString;
        this.cthread = new I2PClientThread(destinationString);

        //Create socket to handle clients
        I2PThread t = new I2PThread();
        t.setName("serverhandler1");
        t.setDaemon(false);
        t.start();
    }

    public I2PClientThread getCthread() {
        return cthread;
    }

}
