import java.net.*;
import java.io.*;
import java.sql.Array;
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
        //System.out.printf("Connection received from %s\n", socket);
        try {
            File f = new File("Listings.txt");
            Market mkt = new Market();
            ArrayList<Store> market = mkt.fromFile(f);
            oos.writeObject(market);
            while (true) {
                String s = (String) reader.readObject();
                switch (s) {
                    case "uLoad":
                        oos.writeObject(getList("UserAccounts.txt"));
                        break;
                    case "uSignup":
                        ArrayList<String> userList = (ArrayList<String>) reader.readObject();
                        userList.add("\n");
                        writeAndAppend(userList, "UserAccounts.txt");
                        break;
                    case "uDelete":
                        oos.writeObject(getList("UserAccounts.txt"));
                        ArrayList<String> accounts = new ArrayList<>();
                        while (true) {
                            String accountString = (String) reader.readObject();
                            if (accountString == null)
                                break;
                            accounts.add(accountString + "\n");
                        }
                        writeToFile(accounts, "UserAccounts.txt");
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
                    case "bBuy":
                        Product buyProduct = new Product((String) reader.readObject());
                        ArrayList<Store> m = mkt.fromFile(new File("Listings.txt"));
                        for (Store store : m) {
                            for (Product product : store.getProducts()) {
                                if (product.getName().equals(buyProduct.getName())
                                        && product.getSeller().equals(buyProduct.getSeller())) {
                                    //System.out.println("found!");
                                    product.decreaseQuantity();
                                }
                            }
                        }
                        mkt.setMarket(m);
                        mkt.toFile();
                        Market.updateListings();
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
                            for (Product productt : store.getProducts()) {
                                oos.writeObject(productt.toString());
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
                        while (true) {
                            String cartName = (String) reader.readObject();
                            if (cartName == null)
                                break;
                            oos.writeObject(getList(cartName));
                        }
                        break;
                    case "sImport":
                        String importName = String.format("%s", reader.readObject());
                        oos.writeObject(getList(importName));
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
                        Store seller = new Store(sellerName);
                        while (true) {
                            String productString = (String) reader.readObject();
                            if (productString == null)
                                break;
                            //System.out.println(productString);
                            seller.addProduct(new Product(productString));
                        }
                        ArrayList<Product> productss = seller.getProducts();
                        for (Product p : productss) {
                            //System.out.println(p.toString());
                        }
                        seller.printToFile(new File(sellerName + ".txt"));
                        break;
                    case "close":
                        Market.updateListings();
                        break;
                    default:
                        //System.out.println("Error with that input");
                        break;
                }
            }
        } catch (SocketException e) {
            //System.out.println("Socket " + socket + " has disconnected.");
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
            //System.out.println("Error reading to file " + name);
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
            //System.out.println("Error writing to file");
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
                fw.write(str);
            }
            fw.close();
        } catch (IOException e) {
            //System.out.println("Error writing to file");
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
