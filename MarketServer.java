import java.net.*;
import java.io.*;
import java.util.*;

public class MarketServer implements Runnable {
    Socket socket;

    public MarketServer(Socket socket) throws IOException {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            File f = new File("Listings.txt");
            BufferedReader br;
            br = new BufferedReader(new FileReader(f));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ArrayList<Store> market = Market.fromFile(f);
        } catch (IOException e) {
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

    public static boolean writeToFile(ArrayList<String> s, File f) {
        try {
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
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket marketServer = new ServerSocket(1234);
        while (true) {
            Socket s = marketServer.accept();
            MarketServer ms = new MarketServer(s);
            new Thread(ms).run();
        }
    }
}
