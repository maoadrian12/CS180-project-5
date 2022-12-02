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
            ArrayList<Store> market = Market.fromFile(f);
            while (true) {
                String s = (String) reader.readObject();
                switch (s) {
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
                    case "file":
                        Market.toFile();
                        break;
                    case "sSetup":
                        oos.writeObject(getList((String) reader.readObject()));
                        break;
                    case "sCart":
                        oos.writeObject(getList("AllPurchases.txt"));
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
                        break;
                    case "sPrint":
                        Store store = (Store) reader.readObject();
                        store.printToFile();
                        break;
                    case "close":
                        Market.updateListings();
                        break;
                    default:
                        System.out.println("Error with that input");
                        break;
                }

            }
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

    public void writeToFile(ArrayList<String> s, String fileName) throws IOException {
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
