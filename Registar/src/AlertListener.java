public interface AlertListener {
    void onUserDisconnected(String username, String ip, Connection con);

    void onUserConnected(Connection con);
}
