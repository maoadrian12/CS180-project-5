import javax.swing.*;
import java.nio.Buffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.*;

import static java.util.Collections.reverseOrder;

/**
 * @author Mao, Chakrabarty, Lee, Johnson, Muthyala
 * @version 11.13.22
 */
public class Sellers extends User {

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ArrayList<Product> productList = new ArrayList<>();
    private Store store;

    /**
     * Sets up a seller given some info.
     *
     * @param email
     * @param password
     */
    public Sellers(String email, String password) {
        super(email, password);
        store = new Store(email);
        setBuyOrSell("seller");
    }

    /**
     * Sets up a seller given a user
     *
     * @param user The user that is a seller
     */
    public Sellers(User user) {
        super(user.getEmail(), user.getPassword());
        setBuyOrSell("seller");
    }

    /**
     * @param email their username
     *              Sets up the text file with all the seller's info
     */
    private void setupInfo(String email) {
        //File f = new File(email + ".txt");
        try {
            oos.writeObject("sSetup");
            oos.writeObject(email + ".txt");
            ArrayList<String> products = (ArrayList<String>) ois.readObject();
            for (String s : products) {
                productList.add(new Product(s));
            }
            store = new Store(email);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        /*if (f.exists()) {
            try {
                fr = new FileReader(f);
                br = new BufferedReader(fr);
                while (true) {
                    String s = br.readLine();
                    if (s == null) {
                        break;
                    }
                    productList.add(new Product(s));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    public void setupSocket(ObjectInputStream ois, ObjectOutputStream oos) {
        this.ois = ois;
        this.oos = oos;
        setupInfo(super.getEmail());
    }

    /**
     * Method that prints the different choices the seller can do
     */
    public synchronized void choices(Sellers seller, ArrayList<Store> market, Scanner input) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        do {
            try {
                update();
                do {
                    String[] choices = {"1. Create Product", "2. Edit Product", "3. Delete Product",
                            "4. See products", "5. See sale statistics", "6. View carts", "7. Import/Export products",
                            "8. Delete account", "9. Exit the application"};
                    try {
                        String answer = (String) (JOptionPane.showInputDialog(null, "Seller Options",
                                "Choice?", JOptionPane.QUESTION_MESSAGE,
                                null, choices, choices[0]));
                        choice = Integer.parseInt(answer.substring(0, 1));
                        break;
                    } catch (InputMismatchException e) {
                        JOptionPane.showMessageDialog(null, "Please select a choice.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } while (true);
                switch (choice) {
                    case 1:
                        String n = "";
                        do {
                            n = JOptionPane.showInputDialog(null, "What is the name of the product?",
                                    "Create Product", JOptionPane.QUESTION_MESSAGE);
                            if (n.length() == 0) {
                                JOptionPane.showMessageDialog(null, "Please enter a name.",
                                        "Create Product", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (n.equals(""));
                        String desc = "";
                        do {
                            desc = JOptionPane.showInputDialog(null, "What is the description of the product?",
                                    "Create Product", JOptionPane.QUESTION_MESSAGE);
                            if (desc.length() == 0) {
                                JOptionPane.showMessageDialog(null, "Please enter a description",
                                        "Create Product", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (desc.equals(""));
                        int q = -1;
                        double p = -1;
                        do {
                            try {
                                q = Integer.parseInt(JOptionPane.showInputDialog(null, "What is the quantity of the product?",
                                        "Create Product", JOptionPane.QUESTION_MESSAGE));
                                if (q < 0) {
                                    q = -1;
                                    JOptionPane.showMessageDialog(null, "Please enter an integer greater than zero.",
                                            "Create Product", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Please enter an integer.",
                                        "Create Product", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (q == -1);
                        do {
                            try {
                                p = Double.parseDouble(JOptionPane.showInputDialog(null, "What is the price of the product?",
                                        "Create Product", JOptionPane.QUESTION_MESSAGE));
                                if (p < 0) {
                                    p = -1;
                                    JOptionPane.showMessageDialog(null, "Please enter an double greater than zero.",
                                            "Create Product", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Please enter an double.",
                                        "Create Product", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (p == -1);
                        createProduct(n, super.getEmail(), desc, q, p);
                        break;
                    case 2:
                        boolean sent = false;
                        String[] editChoices = new String[productList.size()];
                        for (int i = 0; i < editChoices.length; i++) {
                            editChoices[i] = productList.get(i).getName();
                        }
                        do {
                            String editAnswer = (String) JOptionPane.showInputDialog(null, "Which product would you like to edit?",
                                    "Edit product", JOptionPane.INFORMATION_MESSAGE, null, editChoices, editChoices[0]);
                            try {
                                int answer = Arrays.asList(editChoices).indexOf(editAnswer);
                                if (answer == -1) {
                                    sent = true;
                                } else if (answer > productList.size() - 1) {
                                    JOptionPane.showMessageDialog(null,
                                            "Please enter a number between 0 and " + (productList.size() - 1), "choices",
                                            JOptionPane.INFORMATION_MESSAGE, null);
                                } else {
                                    String name = "";
                                    do {
                                        name = JOptionPane.showInputDialog(null, "What is the new name?",
                                                "Edit Product", JOptionPane.QUESTION_MESSAGE);
                                        if (name.length() == 0) {
                                            JOptionPane.showMessageDialog(null, "Please enter a name.",
                                                    "Edit Product", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } while (name.equals(""));
                                    String description = "";
                                    do {
                                        description = JOptionPane.showInputDialog(null, "What is the new description?",
                                                "Edit Product", JOptionPane.QUESTION_MESSAGE);
                                        if (description.length() == 0) {
                                            JOptionPane.showMessageDialog(null, "Please enter a description.",
                                                    "Edit Product", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } while (description.equals(""));
                                    int quantity = -1;
                                    double d = -1;
                                    do {
                                        try {
                                            quantity = Integer.parseInt(JOptionPane.showInputDialog(null, "What is the new quantity?",
                                                    "Edit Product", JOptionPane.QUESTION_MESSAGE));
                                            if (quantity < 0) {
                                                quantity = -1;
                                                JOptionPane.showMessageDialog(null, "Please enter an integer greater than 0.",
                                                        "Edit Product", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } catch (Exception e) {
                                            JOptionPane.showMessageDialog(null, "Please enter an integer.",
                                                    "Edit Product", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } while (quantity == -1);
                                    do {
                                        try {
                                            d = Double.parseDouble(JOptionPane.showInputDialog(null, "What is the new price?",
                                                    "Edit Product", JOptionPane.QUESTION_MESSAGE));
                                            if (d < 0) {
                                                d = -1;
                                                JOptionPane.showMessageDialog(null, "Please enter a double greater than 0.",
                                                        "Edit Product", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } catch (Exception e) {
                                            JOptionPane.showMessageDialog(null, "Please enter a double",
                                                    "Edit Product", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } while (d == -1);
                                    editProduct(productList.get(answer), name, description, quantity, d);
                                    sent = true;
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Please enter an integer.",
                                        "Edit Product", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (!sent);
                        break;
                    case 3:
                        int num = -2;
                        String[] deleteChoices = new String[productList.size()];
                        for (int i = 0; i < deleteChoices.length; i++) {
                            deleteChoices[i] = productList.get(i).getName();
                        }
                        do {
                            String deleteAnswer = (String) JOptionPane.showInputDialog(null, "Which product would you like to edit?(answer -1 to cancel)",
                                    "Delete product", JOptionPane.INFORMATION_MESSAGE, null, deleteChoices, deleteChoices[0]);
                            try {
                                num = Arrays.asList(deleteChoices).indexOf(deleteAnswer);
                                if (num == -1) {
                                    JOptionPane.showMessageDialog(null, "Cancelling...",
                                            "Delete Product", JOptionPane.INFORMATION_MESSAGE);
                                } else if (num < 0) {
                                    JOptionPane.showMessageDialog(null, "Please enter an integer greater than 0.",
                                            "Delete Product", JOptionPane.ERROR_MESSAGE);
                                } else if (num > (productList.size() - 1)) {
                                    JOptionPane.showMessageDialog(null, "Please enter an integer between 0 and " + (productList.size() - 1),
                                            "Delete Product", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    deleteProduct(productList.get(num));
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Please enter an integer",
                                        "Delete Product", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (num == -2);
                        break;
                    case 4:
                        String[] products = new String[productList.size()];
                        if (productList != null && productList.size() != 0) {
                            for (int i = 0; i < productList.size(); i++) {
                                String s = String.format("Name: %s | Description: %s | Quantity: %d | Price: %.2f",
                                        productList.get(i).getName(), productList.get(i).getDesc(), productList.get(i).getQuantity(), productList.get(i).getPrice());
                                products[i] = s;
                            }
                            JOptionPane.showMessageDialog(null, products, "View Products", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "No current products",
                                    "Delete Product", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case 5:
                        viewStatistics(input);
                        break;
                    case 6:
                        viewCarts();
                        break;
                    case 7:
                        sent = true;
                        String[] iORe = new String[]{"Import", "Export"};
                        do {
                            //System.out.println("Import(i) or Export(e)?");
                            String answer = (String) JOptionPane.showInputDialog(null, "Import or Export?",
                                    "Import/Export", JOptionPane.QUESTION_MESSAGE,
                                    null, iORe, iORe[0]);
                            if (answer.equals("Import")) {
                                im(input);
                                sent = false;
                            } else if (answer.equals("Export")) {
                                export();
                                sent = false;
                            } else {
                                JOptionPane.showMessageDialog(null, "How", "Import/Export", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (sent);
                        break;
                    case 8:
                        super.deleteAccount(ois, oos);
                        productList = new ArrayList<>();
                    /*store.setProducts(productList);
                    store.printToFile(new File(super.getEmail() + ".txt"));*/
                        choice = 9;
                        break;
                    case 9:
                        // keep for exit statement
                    /*store.setProducts(productList);
                    store.printToFile(new File(super.getEmail() + ".txt"));*/
                    /*try {
                        oos.writeObject("close");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Enter A Valid Number",
                                "Choice?", JOptionPane.QUESTION_MESSAGE);
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error, exiting the program...",
                        "Error", JOptionPane.ERROR_MESSAGE);
                update();
                break;
            }
        } while (choice != 9);
    }

    /**
     * This method creates a product given some info.
     *
     * @param name     The name of the product
     * @param seller   The seller of the product
     * @param desc     The description of the product
     * @param quantity The quantity available for the product
     * @param price    The price of the product
     */
    public void createProduct(String name, String seller, String desc, int quantity, double price) {
        //creates a new product using individual product attributes and adds it to the seller's list of products
        Product product = new Product(name, seller, desc, quantity, price);
        boolean checkDuplicate = false;
        if (productList.size() > 0) {
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).equals(product))
                    checkDuplicate = true;
            }
        }
        if (!checkDuplicate)
            productList.add(product);
    }

    /**
     * This method creates a new product using a String formatted with all the product's information.
     *
     * @param info The string that creates the product.
     */
    public void createProduct(String info) {
        //creates a new product using a formatted attribute string and adds it to the seller's list of products
        Product product = new Product(info);
        boolean checkDuplicate = false;
        if (productList.size() > 0) {
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).equals(product))
                    checkDuplicate = true;
            }
        }
        if (!checkDuplicate)
            productList.add(product);
    }

    /**
     * This method edits a product to produce a different product.
     *
     * @param product     The product to edit
     * @param newName     The new name of the product
     * @param newDesc     The new description of the product.
     * @param newQuantity The new quantity of the product
     * @param newPrice    The new price of the product
     */
    public void editProduct(Product product, String newName, String newDesc, int newQuantity, double newPrice) {
        //method to change the specified product's attributes in the seller's product list
        int index = -1;
        if (productList.size() > 0) {
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).equals(product))
                    index = productList.indexOf(productList.get(i));
            }
            if (index > -1) {
                productList.get(index).setName(newName);
                productList.get(index).setDesc(newDesc);
                productList.get(index).setQuantity(newQuantity);
                productList.get(index).setPrice(newPrice);
            }
        }

    }

    /**
     * This method deletes a given product from the seller's productList.
     *
     * @param product The product to be deleted.
     */
    public void deleteProduct(Product product) {
        //method to remove a product from the seller's list
        if (productList.size() > 0) {
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).equals(product))
                    productList.remove(productList.get(i));
            }
        }
    }

    /**
     * This method allows the seller to view what is in each buyer's cart.
     */
    public void viewCarts() {
        ArrayList<String> accounts;
        ArrayList<String> productCart = new ArrayList<>();
        int numItems = 0;
        try {
            oos.writeObject("sCart");
            accounts = (ArrayList<String>) ois.readObject();
            for (String s : accounts) {
                String name = s.substring(0, s.indexOf(','));
                s = s.substring(s.indexOf(',') + 1);
                String status = s.substring(s.indexOf(',') + 1);
                if (status.equals("buyer")) {
                    oos.writeObject(name + "Cart.txt");
                    ArrayList<String> carts = (ArrayList<String>) ois.readObject();
                    if (carts.size() > 0) {
                        JOptionPane.showMessageDialog(null, "Customer " + name + " has: ",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                        for (String str : carts) {
                            Product p = new Product(str);
                            numItems++;
                            String desc = String.format("%s, sold by %s, priced at %.2f", p.getName(), p.getSeller(), p.getPrice());
                            productCart.add(desc);
                        }
                        JOptionPane.showMessageDialog(null, productCart.toArray(), "View Cart", JOptionPane.INFORMATION_MESSAGE);
                        productCart.clear();
                    }
                }
            }
            oos.writeObject(null);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Nothing in carts!", "View Carts", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (numItems == 0) {
            JOptionPane.showMessageDialog(null, "Nothing in carts!", "View Carts", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method outputs the seller's statistics.
     */
    public void viewStatistics(Scanner input) {
        ArrayList<String> purchases = null;
        try {
            oos.writeObject("sStats");
            purchases = (ArrayList<String>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> buyers = new ArrayList<>();
        ArrayList<String> allPurchases = new ArrayList<>();
        ArrayList<String> yourSales = new ArrayList<>();

        for (String s : purchases) {
            s = s.substring(s.indexOf(',') + 1);
            s = s.substring(s.indexOf(',') + 1);
            String buyerName = s.substring(0, s.indexOf(','));
            if (!buyers.contains(buyerName)) {
                buyers.add(buyerName);
            }
        }

        for (String s : buyers) {
            int count = 0;
            for (String str : purchases) {
                str = str.substring(str.indexOf(',') + 1);
                str = str.substring(str.indexOf(',') + 1);
                String buyerName = str.substring(0, str.indexOf(','));
                if (buyerName.equalsIgnoreCase(s)) {
                    count++;
                }
            }

            allPurchases.add(count + "," + s);
            //System.out.printf("Buyer %s has bought %d products\n", s, count);
        }

        for (int i = 0; i < productList.size(); i++) {
            int count = 0;
            for (String s : purchases) {
                String productName = productList.get(i).getName();
                s = s.substring(0, s.indexOf(','));
                if (productName.equalsIgnoreCase(s)) {
                    count++;
                }
            }
            yourSales.add(count + "," + productList.get(i).getName());
            JOptionPane.showMessageDialog(null, String.format("You have sold %d of product %s\n", count, productList.get(i).getName()),
                    "Stats", JOptionPane.INFORMATION_MESSAGE);
            //System.out.printf("You have sold %d of product %s\n", count, productList.get(i).getName());
        }

        String answer = JOptionPane.showInputDialog(null, "Would you like to sort?(y/n)", "Sort?",
                JOptionPane.QUESTION_MESSAGE);
        if (answer.equalsIgnoreCase("y")) {
            Collections.sort(allPurchases, reverseOrder());
            Collections.sort(yourSales, reverseOrder());
            ArrayList<String> purchase = new ArrayList<String>();
            for (String str : allPurchases) {
                String numBought = str.substring(0, str.indexOf(','));
                String buyer = str.substring(str.indexOf(',') + 1);
                purchase.add(String.format("Buyer %s has bought %s products\n", buyer, numBought));
                System.out.printf("Buyer %s has bought %s products\n", buyer, numBought);
            }
            JOptionPane.showMessageDialog(null, purchase, "Stats", JOptionPane.INFORMATION_MESSAGE);
            ArrayList<String> yourPurchase = new ArrayList<>();
            for (String string : yourSales) {
                String numBought = string.substring(0, string.indexOf(','));
                String productName = string.substring(string.indexOf(',') + 1);
                yourPurchase.add(String.format("You have sold %s of product %s\n", numBought, productName));
                //System.out.printf("You have sold %s of product %s\n", numBought, productName);
            }
            JOptionPane.showMessageDialog(null, yourPurchase, "Stats", JOptionPane.INFORMATION_MESSAGE);
        }

        /*File f = new File("AllPurchases.txt");
        File f2 = new File("UserAccounts.txt");
        ArrayList<String> buyers = new ArrayList<>();
        try {
            FileReader fr = new FileReader(f2);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                }
                int index = s.lastIndexOf(",");
                String status = s.substring(index + 1);
                if (status.equals("buyer")) {
                    int index2 = s.indexOf(",");
                    String buyerName = s.substring(0, index2);
                    buyers.add(buyerName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, Integer> map = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        for (int i = 0; i < buyers.size(); i++) {
            String buyer = buyers.get(i);
            map.put(buyer, 0);
        }
        try {
            double money = 0;
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                }
                int index1 = s.indexOf(",");
                String productName = s.substring(0, index1);
                s = s.substring(index1 + 1);
                int index2 = s.indexOf(",");
                String seller = s.substring(0, index2);
                s = s.substring(index2 + 1);
                int index3 = s.indexOf(",");
                String buyer = s.substring(0, index3);
                s = s.substring(index3 + 1);
                String price = s;
                if (seller.equals(super.getEmail()))
                    money += Double.parseDouble(price);
                map.put(buyer, map.getOrDefault(buyer, 0) + 1);
            }
            System.out.printf("Total revenue: %.2f\n", money);
            System.out.println("Product sales:");
            for (int i = 0; i < productList.size(); i++) {
                Product prod = productList.get(i);
                String productName = prod.getName();
                FileReader flr = new FileReader(f);
                BufferedReader bfr = new BufferedReader(flr);
                int quant = 0;
                while (true) {
                    String s = bfr.readLine();
                    if (s == null)
                        break;
                    String product = s.substring(0, s.indexOf(','));
                    if (product.equals(productName))
                        quant++;
                }
                map2.put(productName, quant);
                System.out.println(productName + "," + quant);
            }
            System.out.println();
            System.out.println("Customers and how many items they bought:");
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                System.out.println(entry.getKey() + "," + entry.getValue());
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("No purchases have been made!");
        }
        Scanner scan = new Scanner(System.in);
        System.out.println("Would you like to sort the dashboard? (y/n)");
        String choice = scan.nextLine();
        if (choice.equals("y")) {
            List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });
            HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
            for (Map.Entry<String, Integer> aa : list) {
                sortedMap.put(aa.getKey(), aa.getValue());
            }
            List<Map.Entry<String, Integer>> list2 = new LinkedList<Map.Entry<String, Integer>>(map2.entrySet());
            Collections.sort(list2, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });
            HashMap<String, Integer> sortedMap2 = new LinkedHashMap<String, Integer>();
            for (Map.Entry<String, Integer> aa : list2) {
                sortedMap2.put(aa.getKey(), aa.getValue());
            }
            System.out.println("Customers and how many items they bought:");
            for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
                System.out.println(entry.getKey() + "," + entry.getValue());
            }
            System.out.println();
            System.out.println("Product sales:");
            for (Map.Entry<String, Integer> entry : sortedMap2.entrySet()) {
                System.out.println(entry.getKey() + "," + entry.getValue());
            }
            System.out.println();
        } else if (choice.equals("n")) {
        }*/
    }

    /**
     * This method adds a product to the given seller's list of products.
     * If the product already exists, the quantity for that product is increased instead.
     *
     * @param p The product to be added.
     */
    private void addProduct(Product p) {
        boolean duplicate = false;
        for (int i = 0; i < productList.size(); i++) {
            if (p.getName().equals(productList.get(i).getName())) {
                duplicate = true;
                productList.get(i).increaseQuantity(p.getQuantity());
            }
        }
        if (!duplicate) {
            createProduct(p.toString());
        }
    }

    /**
     * This method imports the seller's products from a given file, given that it is the correct format.
     *
     * @param input The scanner that takes user input.
     */
    public void im(Scanner input) {

        String answer = JOptionPane.showInputDialog(null, "What path do you want to import from?",
                "Where?", JOptionPane.QUESTION_MESSAGE);
        ArrayList<String> products = null;
        try {
            oos.writeObject("sImport");
            oos.writeObject(answer);
            products = (ArrayList<String>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //File f = new File(answer);
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error importing from file, aborting...", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            for (String s : products) {
                Product p = new Product(s);
                if (p.getSeller().equals(super.getEmail())) {
                    addProduct(p);
                } else {
                    JOptionPane.showMessageDialog(null, "Please import your own products", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            /*
            try {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                while (true) {
                    String s = br.readLine();
                    if (s == null) {
                        break;
                    }
                    Product p = new Product(s);
                    if (p.getSeller().equals(super.getEmail())) {
                        addProduct(p);
                    } else {
                        System.out.println("Please have your own products.");
                    }
                }
                System.out.println("Finished importing!");
            } catch (IOException e) {
                System.out.println("Error importing from file, aborting...");
            } catch (Exception e) {
                System.out.println("Please path a .csv file with correct formatting for products.");
            }*/
        }
    }

    /**
     * This method exports the seller's shop to a file.
     */
    public void export() {
        JOptionPane.showMessageDialog(null, "Exporting to " + super.getEmail() + ".txt...",
                "Export", JOptionPane.INFORMATION_MESSAGE);
        String s = super.getEmail() + ".csv";
        ArrayList<String> products = new ArrayList<>();
        for (Product p : productList) {
            products.add(p.toString());
        }
        try {
            oos.writeObject(s);
            oos.writeObject(products);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        /*
        try {
            FileWriter fw = new FileWriter(f);
            for (Product p : productList) {
                fw.write(p.toString() + "\n");
            }
            fw.close();
            System.out.println("Exported!");
        } catch (IOException e) {
            System.out.println("Cannot write to that file, sorry!");
        }*/
    }

    public synchronized void update() {
        store.setProducts(productList);
        try {
            ArrayList<Product> storeProducts = store.getProducts();
            oos.writeObject("sPrint");
            oos.writeObject(super.getEmail());
            for (Product p : storeProducts) {
                if (p.toString() != null) {
                    oos.writeObject(p.toString());
                    oos.flush();
                }
            }
            oos.writeObject(null);
            oos.writeObject("close");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}