/**
 * Created by rambou on 22/11/2016.
 */
public interface AlertListener {
    void onUserDisconnected(String username, String ip, Connection con);

    void onUserConnected(Connection con);
}
