import java.awt.*;
import java.io.*;
import java.util.*;


/**
 * @author Mao, Chakrabarty, Lee, Johnson, Muthyala
 * @version 11.13.22
 */
public class Market {
    private static ArrayList<Store> market = new ArrayList<Store>();

    /**
     * This method prints the given market to a file.
     * @param f The file to print the market to
     * @param m The market
     */
    public static void toFile(File f, ArrayList<Store> m) {
        try {
            PrintWriter pw = new PrintWriter(f);
            for (int i = 0; i < m.size(); i++) {
                pw.write(m.get(i).toString());
                pw.println();
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method gets the market from a given file.
     * @param f The file that contains the entire market
     */
    public static void fromFile(File f) {
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            int marketNum = 0;
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            if (!(f.length() == 0)) {
                String sellerName = "";
                while (true) {
                    String s = br.readLine();
                    if (s == null) {
                        break;
                    }
                    if (s.equals("")) {
                        marketNum++;
                    } else if (s.substring(0, 6).equals("Seller")) {
                        sellerName = s.substring(s.indexOf(':') + 2);
                        market.add(new Store(sellerName));
                    } else {
                        market.get(marketNum).addProduct(new Product(s));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Listings.txt not found, creating new file...");
        }
        toFile();
    }

    /**
     * This method prints each store in Market to its given file
     */
    public static void toFile() {
        for (int i = 0; i < market.size(); i++) {
            market.get(i).printToFile();
        }
    }

    /**
     * THis method updates Listings.txt, the file that contains all the information in the market.
     */
    public static void updateListings() {
        try {
            ArrayList<Store> tempList = new ArrayList<>();
            int marketIndex = 0;
            File f = new File("UserAccounts.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                }
                String email = s.substring(0, s.indexOf(','));
                s = s.substring(s.indexOf(',') + 1);
                s = s.substring(s.indexOf(',') + 1);
                String status = s;
                if (status.equalsIgnoreCase("seller")) {
                    tempList.add(new Store(email));
                    File file = new File(email + ".txt");
                    if (file.exists() && f.length() != 0) {
                        FileReader fr2 = new FileReader(file);
                        BufferedReader br2 = new BufferedReader(fr2);
                        while (true) {
                            String str = br2.readLine();
                            if (str == null) {
                                marketIndex++;
                                break;
                            }
                            tempList.get(marketIndex).addProduct(new Product(str));
                        }
                    } else {
                        marketIndex++;
                    }
                }
            }
            toFile(new File("Listings.txt"), tempList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method that starts the entire program.
     * @param args User arguments(useless)
     */
    public static void main(String[] args) {
        fromFile(new File("Listings.txt"));

        User user = User.prompt(); // returns user object
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            System.out.println("Goodbye.");
        } else {
            System.out.println("\nWelcome " + user.getEmail());
        }
        Scanner input = new Scanner(System.in);
        if (user instanceof Buyers) {
            Buyers buyer = new Buyers(user);
            buyer.choices(buyer, market, input);
        } else if (user instanceof Sellers) {
            Sellers seller = new Sellers(user);
            seller.choices(seller, market, input);
        }
        updateListings();
    }
}