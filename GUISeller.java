import java.nio.Buffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.*;
import javax.swing.*;
/**
 * @author Mao, Chakrabarty, Lee, Johnson, Muthyala
 * @version 11.13.22
 */
public class GUISeller extends GUIUser {
    private ArrayList<Product> productList = new ArrayList<>();
    private Store store;

    /**
     * Sets up a seller given some info.
     * @param email
     * @param password
     */
    public GUISeller(String email, String password) {
        super(email, password);
        store = new Store(email);
        setBuyOrSell("seller");
        setupInfo(email);
    }

    /**
     * Sets up a seller given a user
     * @param user The user that is a seller
     */
    public GUISeller(GUIUser user) {
        super(user.getEmail(), user.getPassword());
        setBuyOrSell("seller");
        setupInfo(user.getEmail());
    }

    /**
     * @param email their username
     * Sets up the text file with all the seller's info
     */
    private void setupInfo(String email) {
        FileReader fr;
        BufferedReader br;
        File f = new File(email + ".txt");
        store = new Store(email);
        if (f.exists()) {
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
        }
    }

    /**
     * Method that prints the different choices the seller can do
     */
    public void choices(GUISeller seller, ArrayList<Store> market, Scanner input) {
        try {
            Scanner scanner = new Scanner(System.in);
            int choice = -1;

            do {
                String[] choices = {"1. Create Product", "2. Edit Product", "3. Delete Product",
                        "4. See products", "5. See sale statistics", "6. View carts", "7. Import/Export products",
                        "8. Delete account", "9. Exit the application"};
                do {
                    try {
                        String answer = (String) (JOptionPane.showInputDialog(null, "Seller Options",
                                "Choice?", JOptionPane.QUESTION_MESSAGE,
                                null, choices, choices[0]));
                        choice = Integer.parseInt(answer.substring(0, 1));
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Please enter an integer.");
                        JOptionPane.showMessageDialog(null, "Please enter an integer.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        scanner.nextLine();
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
                        update();
                        break;
                    case 2:
                        boolean sent = false;
                        String[] editChoices = new String[productList.size()];
                        for (int i = 0; i < editChoices.length; i++) {
                            editChoices[i] = String.valueOf(i);
                        }
                        do {
                            String editAnswer = (String) JOptionPane.showInputDialog(null, "Which product would you like to edit?(answer -1 to cancel)",
                                    "Select product", JOptionPane.INFORMATION_MESSAGE, null, editChoices, editChoices[0]);
                            try {
                                int answer = Integer.parseInt(editAnswer);
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
                        update();
                        break;
                    case 3:
                        int num = -2;
                        String[] deleteChoices = new String[productList.size()];
                        for (int i = 0; i < deleteChoices.length; i++) {
                            deleteChoices[i] = String.valueOf(i);
                        }
                        do {
                            String deleteAnswer = (String) JOptionPane.showInputDialog(null, "Which product would you like to edit?(answer -1 to cancel)",
                                    "Select product", JOptionPane.INFORMATION_MESSAGE, null, deleteChoices, deleteChoices[0]);
                            try {
                                num = Integer.parseInt(deleteAnswer);
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
                        update();
                        break;
                    case 4:
                        if (productList != null && productList.size() != 0) {
                            for (Product product : productList) {
                                System.out.println(product.getListing());
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "No current products",
                                    "Delete Product", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case 5:
                        viewStatistics();
                        break;
                    case 6:
                        viewCarts();
                        break;
                    case 7:
                        sent = true;
                        do {
                            System.out.println("Import(i) or Export(e)?");
                            String answer = input.nextLine();
                            if (answer.equals("i")) {
                                im(input);
                                sent = false;
                            } else if (answer.equals("e")) {
                                export();
                                sent = false;
                            } else {
                                System.out.println("Please enter either i or e.");
                            }
                        } while (sent);
                        update();
                        break;
                    case 8:
                        //    super.deleteAccount();
                        productList = new ArrayList<>();
                        store.setProducts(productList);
                        store.printToFile(new File(super.getEmail() + ".txt"));
                        choice = 9;
                        break;
                    case 9:
                        // keep for exit statement
                        store.setProducts(productList);
                        store.printToFile(new File(super.getEmail() + ".txt"));
                        Market.updateListings();
                        break;
                    default:
                        System.out.println("Enter Valid Number");
                        break;
                }
            } while (choice != 9);
        } catch (Exception e) {
            store.setProducts(productList);
            store.printToFile(new File(super.getEmail() + ".txt"));
            Market.updateListings();
        }
    }

    /**
     * This method creates a product given some info.
     * @param name The name of the product
     * @param seller The seller of the product
     * @param desc The description of the product
     * @param quantity The quantity available for the product
     * @param price The price of the product
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
     * @param product The product to edit
     * @param newName The new name of the product
     * @param newDesc The new description of the product.
     * @param newQuantity The new quantity of the product
     * @param newPrice The new price of the product
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
        File f = new File("UserAccounts.txt");
        int numItems = 0;
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                }
                String name = s.substring(0, s.indexOf(','));
                s = s.substring(s.indexOf(',') + 1);
                String status = s.substring(s.indexOf(',') + 1);
                if (status.equals("buyer")) {
                    File file = new File(name + "Cart.txt");
                    if (file.exists() && file.length() != 0) {
                        System.out.println("Customer " + name + " has: ");
                        FileReader flr = new FileReader(file);
                        BufferedReader bfr = new BufferedReader(flr);
                        while (true) {
                            String str = bfr.readLine();
                            if (str == null) {
                                break;
                            }
                            Product p  = new Product(str);
                            numItems++;
                            System.out.printf("\t%s, sold by %s, priced at %.2f\n",
                                    p.getName(), p.getSeller(), p.getPrice());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Nothing in carts!");
        }
        if (numItems == 0) {
            System.out.println("Nothing in carts!");
        }
    }

    /**
     * This method outputs the seller's statistics.
     */
    public void viewStatistics() {
        File f = new File("AllPurchases.txt");
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
                                   Map.Entry<String, Integer> o2)
                {
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
                                   Map.Entry<String, Integer> o2)
                {
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

        }
    }

    /**
     * This method adds a product to the given seller's list of products.
     * If the product already exists, the quantity for that product is increased instead.
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
     * @param input The scanner that takes user input.
     */
    public void im(Scanner input) {
        String answer = JOptionPane.showInputDialog(null, "What path do you want to import from?",
                "Import Product", JOptionPane.QUESTION_MESSAGE);
        File f = new File(answer);
        if (!f.exists() || f.isDirectory() || f.length() == 0) {
            JOptionPane.showMessageDialog(null, "Error importing from file, aborting...",
                    "Import Product", JOptionPane.ERROR_MESSAGE);
        } else {
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
                        JOptionPane.showMessageDialog(null, "Please have your own products.",
                                "Import Product", JOptionPane.ERROR_MESSAGE);
                    }
                }
                JOptionPane.showMessageDialog(null, "Finished importing!",
                        "Import Product", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error importing from file, aborting...",
                        "Import Product", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Please path a .csv file with correct formatting for products.",
                        "Import Product", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method exports the seller's shop to a file.
     */
    public void export() {
        System.out.println("Exporting to " + super.getEmail() + ".csv...");
        File f = new File(super.getEmail() + ".csv");
        try {
            FileWriter fw = new FileWriter(f);
            for (Product p : productList) {
                fw.write(p.toString() + "\n");
            }
            fw.close();
            System.out.println("Exported!");
            JOptionPane.showMessageDialog(null, "Exported!",
                    "Export Product", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            System.out.println("Cannot write to that file, sorry!");
            JOptionPane.showMessageDialog(null, "Cannot write to that file, sorry!",
                    "Export Product", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void update() {
        store.setProducts(productList);
        store.printToFile(new File(super.getEmail() + ".txt"));
        Market.updateListings();
    }

}
