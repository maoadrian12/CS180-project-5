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
        //BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        //oos.writeObject("Hello!");
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        //String s = reader.readLine();
        oos.writeObject("setup");

        ArrayList<Store> market = (ArrayList<Store>) ois.readObject();

        User user = User.prompt(); // returns user object
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            System.out.println("Goodbye.");
        } else {
            System.out.println("\nWelcome " + user.getEmail());
        }
        if (user instanceof Buyers) {
            Buyers buyer = new Buyers(user);
            buyer.setupSocket(ois, writer, oos);
            buyer.choices(buyer, market, input);
        } else if (user instanceof Sellers) {
            Sellers seller = new Sellers(user);
            seller.setupSocket(ois, oos);
            seller.choices(seller, market, input);
        } else {
            System.out.println("IDK what happened");
        }
        oos.writeObject("close");
    }
}
