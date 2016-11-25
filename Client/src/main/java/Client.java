import java.io.Serializable;

public class Client implements Serializable {

    private String username;
    private String i2pUrl;

    public Client(String username, String i2pUrl) {
        this.username = username;
        this.i2pUrl = i2pUrl;
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
}
