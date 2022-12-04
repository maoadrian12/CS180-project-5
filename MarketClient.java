import java.net.*;
import java.io.*;
import java.util.*;

public class MarketClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner input = new Scanner(System.in);
        System.out.println("Port?");
        int port = input.nextInt();
        input.nextLine();
        System.out.println("Host?");
        String host = input.nextLine();
        Socket socket = new Socket(host, port);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ArrayList<Store> market = (ArrayList<Store>) ois.readObject();
        for (Store s : market) {
            System.out.println(s);
        }
        User user = User.prompt(); // returns user object
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            System.out.println("Goodbye.");
        } else {
            System.out.println("\nWelcome " + user.getEmail());
        }
        if (user instanceof Buyers) {
            Buyers buyer = new Buyers(user);
            buyer.setupSocket(ois, oos);
            buyer.choices(buyer, market, input);
        } else if (user instanceof Sellers) {
            Sellers seller = new Sellers(user);
            //seller.setupSocket(ois, oos);
            seller.choices(seller, market, input);
        }
        Market.updateListings();
    }
}
