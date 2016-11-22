import java.io.IOException;
import java.util.HashMap;

/**
 * Created by rambou on 22/11/2016.
 */
public class AlertHandler implements AlertListener {

    private Registrar registrar;

    public AlertHandler(Registrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public void onUserDisconnected(String username, String ip, Connection con) {

        System.out.println(username + " disconnected from " + ip);
        // αφαίρεση του χρήστη από την λίστα
        registrar.getClients().remove(con);

        // για κάθε ενεργή σύνδεση στον registrar
        for (Connection conn : registrar.getClients()) {
            try {
                // Αποστολή της λίστας με τους εναπομείναντες συνδεμένους client.
                conn.getOut().writeObject(convertToHashMap());
            } catch (IOException e) {
                // Αφαίρεση αν αποτύχει η αποστολη επειδή ο χρήστης πιθανόν είναι αποσυνδεμένος
                registrar.getRegClients().remove(username);
            }
        }
    }

    @Override
    public void onUserConnected(Connection con) {
        // για κάθε ενεργή σύνδεση στον registrar
        for (Connection conn : registrar.getClients()) {
            try {
                // Αποστολή της λίστας με τους εναπομείναντες συνδεμένους client.
                conn.getOut().writeObject(convertToHashMap());
            } catch (IOException e) {
                // Αφαίρεση αν αποτύχει η αποστολη επειδή ο χρήστης πιθανόν είναι αποσυνδεμένος
                registrar.getClients().remove(con);
            }
        }
    }

    private HashMap<String, String> convertToHashMap() {
        HashMap<String, String> regClients = new HashMap<>();

        for (Connection conn : registrar.getClients()) {
            regClients.put(conn.getClient().getUsername(), conn.getClient().getI2pUrl());
        }
        return regClients;
    }
}
