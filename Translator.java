import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JOptionPane;

public class Translator {
    private static Socket socket;
    //TODO: use this for... something
    private static boolean connected;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;

    public Translator() {
        // There is only socket active at any time.
        if (socket == null) {
            try {
                socket = new Socket("localhost", Constants.port);
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                connected = true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "We can't connect to the server right now. Please try again later", "Error", JOptionPane.ERROR_MESSAGE);
                connected = false;
            }
        }
    }

    public Object query(Query q) throws Exception {
        try {
            Object result = null;
            oos.writeObject(q);
            oos.flush();
            result = ois.readObject();
            return result;
        } catch (Exception e) {
            throw new Exception("We are having trouble connecting to the server");
        }
    }

    /*
     * Rewritten methods for convenience
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, String> get(String key, String value) throws Exception {
        Object o = query(new Query("Database", "get", new String[]{key, value}));
        if (o == null) {
            return null;
        }
        return (HashMap<String, String>) o;
    }
}