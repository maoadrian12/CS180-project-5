import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import java.sql.SQLOutput;
import java.util.*;

public class MarketServer implements Runnable {
    Socket socket;
    ObjectInputStream reader;
    ObjectOutputStream oos;
    public MarketServer(Socket socket) {
        this.socket = socket;
        try {
            reader = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void run() {
        System.out.printf("Connection received from %s\n", socket);
        try {
            File f = new File("Listings.txt");
            File userFile = new File("UserAccounts.txt");
            Market mkt = new Market();
            ArrayList<Store> market = mkt.fromFile(f);  //Out of bounds error on this line
            oos.writeObject(market);

            while (true) {
                String s = (String) reader.readObject();
                //System.out.println(s);
                switch (s) {
                    case "load":
                        ArrayList<String> allUsers = new ArrayList<>();
                        userFile.createNewFile();
                        FileReader fr = new FileReader(userFile);
                        BufferedReader bfr = new BufferedReader(fr);

                        String line;
                        while ((line = bfr.readLine()) != null)
                            allUsers.add(line);

                        oos.writeObject(allUsers);
                        break;
                    case "signup":
                        String attemptedEmail = (String) reader.readObject();
                        String attemptedPassword = (String) reader.readObject();
                        String buyerOrSeller = (String) reader.readObject();

                        try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
                            userFile.createNewFile();
                            String userData = String.format("%s,%s,%s", attemptedEmail, attemptedPassword, buyerOrSeller);
                            pw.println(userData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "delete":
                        ArrayList<String> accounts = new ArrayList<>();
                        if (!userFile.exists() || userFile.isDirectory()) {
                            System.out.println("Error deleting account, try closing and rerunning the program.");
                        } else {
                            try {
                                FileReader delFr = new FileReader(userFile);
                                BufferedReader br = new BufferedReader(delFr);
                                while (true) {
                                    String s1 = br.readLine();
                                    if (s1 == null) {
                                        break;
                                    }
                                    accounts.add(s1);
                                }
                            } catch (IOException e) {
                                System.out.println("Error deleting account, try closing and rerunning the program.");
                            }
                        }
                        //bring back accounts from users
                        accounts = (ArrayList<String>) reader.readObject();
                        try {
                            FileWriter fw = new FileWriter(f);
                            for (String s1 : accounts) {
                                fw.write(s + "\n");
                            }
                            fw.close();
                            System.out.println("Account deleted, terminating program...");
                        } catch (IOException e) {
                            System.out.println("Error deleting account, try closing and rerunning the program.");
                        }
                        break;
                    case "setup":
                        oos.writeObject(market);
                        break;
                    case "bCart":
                        String email = (String) reader.readObject();
                        oos.writeObject(getList(email + "Cart.txt"));
                        break;
                    case "bSave":
                        ArrayList<String> cart = (ArrayList<String>) reader.readObject();
                        String fileName = (String) reader.readObject();
                        writeToFile(cart, fileName);
                        break;
                    case "bClear":
                        String name = (String) reader.readObject();
                        ArrayList<String> blank = new ArrayList<>();
                        blank.add("");
                        writeToFile(blank, name);
                        break;
                    case "bAdd":
                        ArrayList<String> newPurchases = (ArrayList<String>) reader.readObject();
                        ArrayList<String> newAllPurchases = (ArrayList<String>) reader.readObject();
                        String purchaseFile = (String) reader.readObject();
                        String allPurchase = (String) reader.readObject();
                        writeAndAppend(newPurchases, purchaseFile);
                        writeAndAppend(newAllPurchases, allPurchase);
                        break;
                    case "bHistory":
                        String history = (String) reader.readObject();
                        oos.writeObject(getList(history));
                        break;
                    case "bStats":
                        oos.writeObject(getList("AllPurchases.txt"));
                        break;
                    case "bRefresh":
                        ArrayList<Store> market2 = mkt.fromFile(new File("Listings.txt"));
                        for (Store store : market2) {
                            for (Product p : store.getProducts()) {
                                oos.writeObject(p.toString());
                            }
                        }
                        oos.writeObject(null);
                        mkt.setMarket(market2);
                        mkt.toFile();
                        mkt.fromFile(new File("Listings.txt"));
                        break;
                    case "bExit":
                        ArrayList<Store> buyerMarket = (ArrayList<Store>) reader.readObject();
                        Market market3 = new Market();
                        market3.setMarket(buyerMarket);
                        market3.toFile();
                        break;
                    case "file":
                        mkt.toFile();
                        break;
                    case "sSetup":
                        oos.writeObject(getList((String) reader.readObject()));
                        break;
                    case "sCart":
                        oos.writeObject(getList("UserAccounts.txt"));
                        oos.writeObject(getList((String) reader.readObject()));
                        break;
                    case "sImport":
                        oos.writeObject(getList((String) reader.readObject()));
                        break;
                    case "sExport":
                        String nameOfFile = (String) reader.readObject();
                        ArrayList<String> products = (ArrayList<String>) reader.readObject();
                        writeToFile(products, nameOfFile);
                        break;
                    case "sStats":
                        oos.writeObject(getList("AllPurchases.txt"));
                        break;
                    case "sPrint":
                        String sellerName = (String) reader.readObject();
                        System.out.println(sellerName);
                        Store seller = new Store(sellerName);
                        while (true) {
                            String productString = (String) reader.readObject();
                            if (productString == null)
                                break;
                            System.out.println(productString);
                            seller.addProduct(new Product(productString));
                        }
                        ArrayList<Product> productss = seller.getProducts();
                        for (Product p : productss) {
                            System.out.println(p.toString());
                        }
                        seller.printToFile(new File(sellerName + ".txt"));
                        break;
                    case "close":
                        Market.updateListings();
                        break;
                    default:
                        System.out.println("Error with that input");
                        break;
                }

            }
        } catch (SocketException e) {
            System.out.println("Socket " + socket + " has disconnected.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<String> getList(String name) {
        ArrayList<String> list = new ArrayList<>();
        try {
            File f = new File(name);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String s = br.readLine();
                if (s == null)
                    break;
                list.add(s);
            }
        } catch (IOException e) {
            System.out.println("Error reading to file " + name);
        }
        return list;
    }

    public boolean writeAndAppend(ArrayList<String> s, String fileName) {
        try {
            File f = new File(fileName);
            FileWriter fw = null;
            if (!f.exists() || f.length() == 0) {
                fw = new FileWriter(f);
            } else {
                fw = new FileWriter(f, true);
            }
            for (String str : s) {
                fw.write(str);
            }
            fw.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
            return false;
        }
    }

    public static void writeToFile(ArrayList<String> s, String fileName) throws IOException {
        try {
            File f = new File(fileName);
            FileWriter fw = null;
            if (!f.exists() || f.length() == 0) {
                fw = new FileWriter(f);
            } else {
                fw = new FileWriter(f, false);
            }
            for (String str : s) {
                fw.write(str + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error writing to file");
            throw new IOException();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket marketServer = new ServerSocket(1234);
        while (true) {
            Socket s = marketServer.accept();
            MarketServer ms = new MarketServer(s);
            new Thread(ms).start();
        }
    }
}
