import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class MarketClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner input = new Scanner(System.in);
        int port = -1;
        do {
            String portString = JOptionPane.showInputDialog(null, "Port?", "Use 1234",
                    JOptionPane.INFORMATION_MESSAGE);
            try {
                port = Integer.parseInt(portString);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Please enter an integer.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (port == -1);
        String host = JOptionPane.showInputDialog(null, "Host?", "host", JOptionPane.INFORMATION_MESSAGE);
        Socket socket = null;
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Connection unsuccessful, terminating program...",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ArrayList<Store> market = (ArrayList<Store>) ois.readObject();
        User user = User.prompt(ois, oos); // returns user object
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            JOptionPane.showMessageDialog(null, "Goodbye", "bye", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Welcome " + user.getEmail(),
                    "Welcome!", JOptionPane.INFORMATION_MESSAGE);
        }
        if (user instanceof Buyers) {
            Buyers buyer = new Buyers(user);
            buyer.setupSocket(ois, oos);
            buyer.choices(buyer, market, input);
        } else if (user instanceof Sellers) {
            Sellers seller = new Sellers(user);
            seller.setupSocket(ois, oos);
            seller.choices(seller, market, input);
        }
        Market.updateListings();
    }
}
