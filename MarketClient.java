import java.net.*;
import java.io.*;
import java.util.*;

public class MarketClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner input = new Scanner(System.in);
        int port = -1;
        do {
            System.out.println("Port?");
            try {
                port = input.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer.");
            }
        } while (port == -1);
        input.nextLine();
        System.out.println("Host?");
        String host = input.nextLine();
        Socket socket = null;
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            System.out.println("Connection unsuccessful, terminating program...");
            System.exit(1);
        }
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ArrayList<Store> market = (ArrayList<Store>) ois.readObject();
        for (Store s : market) {
            System.out.println(s);
            //THIS IS FOR DEBUGGING
        }
        User user = User.prompt(ois, oos); // returns user object
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
            seller.setupSocket(ois, oos);
            seller.choices(seller, market, input);
        }
        Market.updateListings();
    }
}
