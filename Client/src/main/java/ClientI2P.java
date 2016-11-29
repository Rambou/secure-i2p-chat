import java.io.Serializable;

public class ClientI2P implements Serializable {

    private String username;
    private String i2pUrl;
    private I2PClient I2p;

    public ClientI2P(String username, String i2pUrl) {
        this.username = username;
        this.i2pUrl = i2pUrl;
        this.I2p = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getI2pUrl() {
        return i2pUrl;
    }

    public void setI2pUrl(String i2pUrl) {
        this.i2pUrl = i2pUrl;
    }

    public I2PClient getI2p() {
        return I2p;
    }

    public void setI2p(I2PClient i2p) {
        I2p = i2p;
    }
}
