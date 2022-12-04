import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.*;
import java.util.*;

import static java.util.Collections.*;

/**
 * @author Mao, Chakrabarty, Lee, Johnson, Muthyala
 * @version 11.13.22
 */
public class Buyers extends User {

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    //private PrintWriter writer;
    private ArrayList<Product> shoppingCart = new ArrayList<>();

    /**
     * A method that creates a buyer given their information
     *
     * @param email    The buyer's email
     * @param password The buyer's password
     */
    public Buyers(String email, String password) {
        super(email, password);
        setBuyOrSell("buyer");
    }

    /**
     * This method creates a buyer given a user
     *
     * @param user The user that is a buyer
     */
    public Buyers(User user) {
        super(user.getEmail(), user.getPassword());
        setBuyOrSell("buyer");
        //setupFile();
    }


    /**
     * This method sets up the file that belongs to the buyer, which contains their purchase history
     * Not certain if we need this method so I commented it out
     */
    /*public void setupFile() {
        //TODO
        File f = new File(super.getEmail() + ".txt");
    }*/

    /**
     * This method sets up the cart of the buyer. If they already have a cart,
     * then it is saved in their cart, and it initializes it
     * from that file. Otherwise, it creates a new file for their cart.
     */
    public void setupCart() {
        try {
            oos.writeObject("bCart");
            ArrayList<String> cart = null;
            try {
                oos.writeObject(super.getEmail());
                cart = (ArrayList<String>) ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            //ArrayList<String> cart = MarketServer.getList(super.getEmail() + "Cart.txt");
            for (String s : cart) {
                shoppingCart.add(new Product(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupSocket(ObjectInputStream ois, ObjectOutputStream oos) {
        this.ois = ois;
        //this.writer = writer;
        this.oos = oos;
    }

    /**
     * This method saves the buyer's current cart to their given cart file.
     */
    public void saveCart() {
        try {
            oos.writeObject("bSave");
            //writer.write(super.getEmail() + "Cart.txt");
            File f = new File(super.getEmail() + "Cart.txt");
            ArrayList<String> stringCart = new ArrayList<>();
            for (Product p : shoppingCart) {
                stringCart.add(p.toString());
            }
            oos.writeObject(stringCart);
            oos.writeObject(super.getEmail() + "Cart.txt");
        } catch (IOException e) {
            System.out.println("Cannot write object");
            e.printStackTrace();
        }
    }

    /**
     * This method adds a product to their cart
     *
     * @param p The product to add to their cart
     */
    private void addToCart(Product p) {
        if (p.getQuantity() > 0) {
            shoppingCart.add(p);
            p.decreaseQuantity();
        } else {
            System.out.println("Out of stock!");
        }
    }

    /**
     * This method clears the buyer's cart
     */
    private void clearCart() {
        try {
            oos.writeObject("bClear");
            oos.writeObject(super.getEmail() + "Cart.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* ArrayList<String> blank = new ArrayList<>();
        blank.add("");
        File f = new File(super.getEmail() + "Cart.txt");
        writer.write("");
        MarketServer.writeToFile(blank, f);*/
    }

    /**
     * This method adds the buyer's cart to their given purchase history.
     * This method is used when a buyer purchases something.
     *
     * @param cart The seller's cart.
     */
    private void addToFile(ArrayList<Product> cart) {
        try {
            oos.writeObject("bAdd");
            File f = new File(super.getEmail() + ".txt");
            ArrayList<String> newPurchases = new ArrayList<>();
            for (Product p : cart) {
                newPurchases.add(p.getName() + "," + p.getSeller() + "\n");
            }
            ArrayList<String> newAllPurchases = new ArrayList<>();
            for (Product p : cart) {
                newAllPurchases.add(p.getName() + "," + p.getSeller() + "," + super.getEmail() + "," + p.getPrice() + "\n");
            }
            File file = new File("AllPurchases.txt");
            oos.writeObject((ArrayList<String>) newPurchases);
            oos.writeObject((ArrayList<String>) newAllPurchases);
            oos.writeObject(super.getEmail() + ".txt");
            oos.writeObject("AllPurchases.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearCart();
        //MarketServer.writeToFile(newPurchases, f);
        //MarketServer.writeToFile(newAllPurchases, file);
        /*
        FileWriter fw;
        try {
            if (!f.exists()) {
                fw = new FileWriter(f);
            }
            fw = new FileWriter(f, true);
            for (Product p : cart) {
                fw.write(p.getName() + "," + p.getSeller() + "\n");
            }
            fw.close();
            FileWriter flw = new FileWriter(file, true);
            for (Product p : cart) {
                flw.write(p.getName() + "," + p.getSeller() + "," + super.getEmail() + "," + p.getPrice() + "\n");
            }
            flw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    /**
     * This method prints out the choices the buyer can make when starting the program.
     *
     * @param buyer  The buyer
     * @param market The arraylist of stores that is the market
     * @param input  The scanner that is used for user input
     */
    public void choices(Buyers buyer, ArrayList<Store> market, Scanner input) {
        setupCart();
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        ArrayList<Product> allProducts = getAllProducts(market);
        do {
            System.out.println("Would you like to:");
            System.out.println("\t1. View current listings");
            System.out.println("\t2. Search for specific products");
            System.out.println("\t3. Sort the market based on price/quantity");
            System.out.println("\t4. Purchase items from product page");
            System.out.println("\t5. View purchase history");
            System.out.println("\t6. Checkout");
            System.out.println("\t7. Edit cart");
            System.out.println("\t8. Export purchase history");
            System.out.println("\t9. View statistics");
            System.out.println("\t10. Delete account");
            System.out.println("\t11. Exit the application");
            do {
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter an integer.");
                    scanner.nextLine();
                }
            } while (true);
            switch (choice) {
                case 1:
                    if (allProducts.size() == 0) {
                        System.out.println("No products are currently listed.");
                        break;
                    }
                    for (Product p : allProducts) {
                        System.out.println("Seller Name: " + p.getSeller());
                        System.out.println(p.getListing());
                        System.out.println();
                    }
                    System.out.println("Would you like to see a specific product? (y/n)");
                    String ans = scanner.nextLine();
                    if (ans.equals("y")) {
                        boolean found = false;
                        System.out.println("What is the name of the product you want to see?");
                        String productName = scanner.nextLine();
                        for (Store store : market) {
                            ArrayList<Product> p = store.getProducts();
                            for (Product pr : p) {
                                if (pr.getName().equals(productName)) {
                                    System.out.println(pr.getPage());
                                    found = true;
                                }
                            }
                        }
                        if (found == false) {
                            System.out.println("No product matching that name.");
                        }
                    } else if (ans.equals("n")) {

                    }
                    break;
                case 2:
                    if (allProducts.size() == 0) {
                        System.out.println("No products are currently listed.");
                        break;
                    }
                    System.out.println("What would you like to search?");
                    ArrayList<Product> pro = new ArrayList<>();
                    String search = scanner.nextLine();
                    boolean found = false;
                    for (Store store : market) {
                        int count = 0;
                        ArrayList<Product> p = store.getProducts();
                        for (Product pr : p) {
                            if (pr.getName().contains(search) || pr.getDesc().contains(search)) {
                                System.out.println(count++ + ": " + pr.getListing());
                                pro.add(pr);
                                found = true;
                            }
                        }
                    }
                    if (found == false) {
                        System.out.println("No products matching that name.");
                    }
                    break;
                case 3:

                    if (allProducts.size() == 0) {
                        System.out.println("No products are currently listed.");
                        break;
                    }
                    int sortChoice = -1;
                    do {
                        try {
                            System.out.println("Sort on quantity or price? (1/2)");
                            sortChoice = scanner.nextInt();
                            scanner.nextLine();
                        } catch (InputMismatchException e) {
                            System.out.println("Please enter either 1 or 2.");
                            scanner.nextLine();
                        }
                        if (sortChoice == 1) {
                            System.out.println("Sorting by quantity");
                            for (int i = 0; i < allProducts.size(); i++) {
                                for (int j = 1; j < allProducts.size() - i; j++) {
                                    if (allProducts.get(j - 1).getQuantity() <
                                            allProducts.get(j).getQuantity()) {
                                        Product temp = allProducts.get(j - 1);
                                        allProducts.set(j - 1, allProducts.get(j));
                                        allProducts.set(j, temp);
                                    }
                                }
                            }

                        } else if (sortChoice == 2) {
                            System.out.println("Sorting by price");
                            for (int i = 0; i < allProducts.size(); i++) {
                                for (int j = 1; j < allProducts.size() - i; j++) {
                                    if (allProducts.get(j - 1).getPrice() > allProducts.get(j).getPrice()) {
                                        Product temp = allProducts.get(j - 1);
                                        allProducts.set(j - 1, allProducts.get(j));
                                        allProducts.set(j, temp);
                                    }
                                }
                            }

                        } else {
                            System.out.println("Invalid Input");
                        }
                    } while (sortChoice != 1 && sortChoice != 2);

                    for (Product p : allProducts) {
                        System.out.println("Seller Name: " + p.getSeller());
                        System.out.println(p.getListing());
                        System.out.println();
                    }
                    break;
                case 4:
                    if (allProducts.size() == 0) {
                        System.out.println("No products are currently listed.");
                        break;
                    }
                    System.out.println("What item do you want to add to your cart?");
                    String item = input.nextLine();
                    boolean find = false;
                    for (Store m : market) {
                        for (Product p : m.getProducts()) {
                            if (p.getName().equals(item)) {
                                int num = -1;
                                do {
                                    System.out.println("How many do you want to add to your cart?");
                                    if (input.hasNextInt()) {
                                        num = input.nextInt();
                                        input.nextLine();
                                        if (num < 0 || num > p.getQuantity()) {
                                            System.out.println("Error adding that many to cart.");
                                            break;
                                        } else {
                                            for (int i = 0; i < num; i++) {
                                                addToCart(p);
                                            }
                                            System.out.println("Added!");
                                        }
                                    } else {
                                        input.nextLine();
                                        System.out.println("Please enter an integer.");
                                    }
                                } while (num == -1);
                                find = true;
                            }
                        }
                    }
                    if (!find) {
                        System.out.println("No item matching that name!");
                    }
                    break;
                case 5:
                    ArrayList<String> history = null;
                    try {
                        oos.writeObject("bHistory");
                        System.out.println("Purchase history:");
                        File f = new File(super.getEmail() + ".txt");
                        oos.writeObject(super.getEmail() + ".txt");
                        history = (ArrayList<String>) ois.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (history.size() == 0) {
                        System.out.println("No purchased items.");
                    } else {
                        for (String s : history) {
                            String name = s.substring(0, s.indexOf(','));
                            String seller = s.substring(s.indexOf(',') + 1);
                            System.out.println("\t" + name + " sold by " + seller);
                        }
                    }
                    break;
                case 6:
                    if (shoppingCart.isEmpty()) {
                        System.out.println("Nothing in your cart!");
                    } else {
                        System.out.println("You are purchasing: ");
                        for (Product p : shoppingCart) {
                            System.out.println("Name: " + p.getName());
                        }
                        System.out.println("Proceed? (y/n)");
                        String answer = input.nextLine();
                        if (answer.equals("y")) {
                            System.out.println("Verify purchase with your password.");
                            String password = input.nextLine();
                            if (password.equals(super.getPassword())) {
                                System.out.println("Purchase completed!");
                                addToFile(shoppingCart);
                                shoppingCart = new ArrayList<Product>();
                                clearCart();
                            } else {
                                System.out.println("Incorrect password, purchase cancelled.");
                            }
                        } else {
                            System.out.println("Purchase cancelled");
                        }
                    }
                    break;
                case 7:
                    if (shoppingCart.isEmpty()) {
                        System.out.println("Nothing in your cart!");
                        break;
                    }
                    System.out.println("Your cart has:");
                    int count = 0;
                    for (Product p : shoppingCart) {
                        System.out.println(count++ + ": " + p.getName());
                    }
                    count = -2;
                    do {
                        System.out.println("Would you like remove a product?(enter a number, or -1 to cancel)");
                        if (input.hasNextInt()) {
                            count = input.nextInt();
                            input.nextLine();
                            if (count == -1) {
                                System.out.println("Cancelling...");
                            } else {
                                try {
                                    System.out.println("Removed " + shoppingCart.get(count).getName());
                                    shoppingCart.get(count).increaseQuantity();
                                    shoppingCart.remove(count);
                                } catch (Exception e) {
                                    System.out.println("Please enter a valid integer.");
                                    count = -2;
                                }
                            }
                        } else {
                            input.nextLine();
                            System.out.println("Please enter an integer.");
                        }
                    } while (count == -2);
                    break;
                case 8:
                    export();
                    break;
                case 9:
                    // keep for exit statement
                    statistics(input);
                    break;
                case 10:
                    super.deleteAccount();
                    choice = 11;
                    break;
                case 11:
                    saveCart();
                    Market mkt = new Market();
                    mkt.setMarket(market);
                    mkt.toFile();
                    /*try {
                        oos.writeObject("file");
                        oos.writeObject("close");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    break;
                default:
                    System.out.println("Enter Valid Number");
                    break;
            }
        } while (choice != 11);
    }

    /**
     * This method exports purchase history to a given file.
     */
    private void export() {
        System.out.println("Your purchase history has been exported to the file " + super.getEmail() + ".txt");

    }

    /**
     * This method returns the arraylist of the products
     *
     * @param market The market
     * @return The market
     */
    public ArrayList<Product> getAllProducts(ArrayList<Store> market) {
        ArrayList<Product> allProducts = new ArrayList<>();
        for (Store store : market) {
            if (!store.isEmpty()) {
                ArrayList<Product> p = store.getProducts();
                for (Product product : p) {
                    allProducts.add(product);
                }
            }
        }
        return allProducts;
    }

    /**
     * This method prints out the statistics for the buyer.
     *
     * @param input The scanner used for user input
     */
    public void statistics(Scanner input) {
        ArrayList<String> purchases = null;
        try {
        oos.writeObject("bStats");
            purchases = (ArrayList<String>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //FileReader fr;
        //BufferedReader br;
        ArrayList<String> sellers = new ArrayList<>();
        ArrayList<String> allPurchases = new ArrayList<>();
        ArrayList<String> yourPurchases = new ArrayList<>();
        for (String s : purchases) {
            s = s.substring(s.indexOf(',') + 1);
            String sellerName = s.substring(0, s.indexOf(','));
            if (!sellers.contains(sellerName)) {
                sellers.add(sellerName);
            }
        }
        for (String s : sellers) {
            int count = 0;
            for (String str : purchases) {
                str = str.substring(str.indexOf(',') + 1);
                String sellerName = str.substring(0, str.indexOf(','));
                if (sellerName.equalsIgnoreCase(s)) {
                    count++;
                }
            }

            allPurchases.add(count + "," + s);
            System.out.printf("Seller %s has sold %d products\n", s, count);

        }
        for (String s : sellers) {
            int count = 0;
            for (String str : purchases) {
                str = str.substring(str.indexOf(',') + 1);
                String sellerName = str.substring(0, str.indexOf(','));
                str = str.substring(str.indexOf(',') + 1);
                String buyerName = str.substring(0, str.indexOf(','));
                if (sellerName.equalsIgnoreCase(s) && buyerName.equals(super.getEmail())) {
                    count++;
                }
            }
            yourPurchases.add(count + "," + s);
            System.out.printf("You have bought %d products from %s\n", count, s);
        }

        System.out.println("Would you like to sort?(y/n)");
        String answer = input.nextLine();
        if (answer.equalsIgnoreCase("y")) {
            Collections.sort(allPurchases, reverseOrder());
            Collections.sort(yourPurchases, reverseOrder());
            for (String str : allPurchases) {
                String numBought = str.substring(0, str.indexOf(','));
                String seller = str.substring(str.indexOf(',') + 1);
                System.out.printf("Seller %s has sold %s products\n", seller, numBought);
            }
            System.out.println("-----------------------");
            for (String string : yourPurchases) {
                String numBought = string.substring(0, string.indexOf(','));
                String seller = string.substring(string.indexOf(',') + 1);
                System.out.printf("You have bought %s products from %s\n", numBought, seller);
            }
        }
    }

    public void updateCart() {
        saveCart();
        try {
            oos.writeObject("file");
            oos.writeObject("close");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Product> refresh() {
        try {
            oos.writeObject("bRefresh");
            return getAllProducts((ArrayList<Store>) ois.readObject());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}