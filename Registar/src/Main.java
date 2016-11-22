import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        // Ξεκίνα τον Registrar Server στην 4000 εκτός και
        // αν ο χρήστης την έχει δώσει ως παράμετρο
        if (args.length == 0) {
            new Registrar(4000);
        } else {
            new Registrar(Integer.valueOf(args[0]));
        }
    }
}
