import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by rambou on 22/11/2016.
 */
public class AlertHandler implements AlertListener {

    private Registrar registrar;

    public AlertHandler(Registrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public void onUserDisconnected(String username, String ip) {

        System.out.println(username + " disconnected from " + ip);
        // αφαίρεση του χρήστη από την λίστα
        registrar.getClients().remove(username);

        // για κάθε ενεργή σύνδεση στον registrar
        for (SSLSocket sock : registrar.getClients()) {
            try {
                ObjectOutputStream ObjOut = new ObjectOutputStream(sock.getOutputStream());
                // Αποστολή της λίστας με τους εναπομείναντες συνδεμένους client.
                ObjOut.writeObject(registrar.getRegClients());
            } catch (IOException e) {
                // Αφαίρεση αν αποτύχει η αποστολη επειδή ο χρήστης πιθανόν είναι αποσυνδεμένος
                registrar.getRegClients().remove(sock);
            }
        }
    }
}
