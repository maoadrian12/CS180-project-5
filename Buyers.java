import javax.swing.*;
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
    private ArrayList<Product> allProducts;
    private ObjectOutputStream oos;
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
    }

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

    public void setupSocket(ObjectInputStream objectIn, ObjectOutputStream objectOut) {
        this.ois = objectIn;
        //this.writer = writer;
        this.oos = objectOut;
    }

    /**
     * This method saves the buyer's current cart to their given cart file.
     */
    public void saveCart() {
        try {
            oos.writeObject("bSave");
            //writer.write(super.getEmail() + "Cart.txt");
            ArrayList<String> stringCart = new ArrayList<>();
            for (Product p : shoppingCart) {
                stringCart.add(p.toString() + "\n");
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
            try {
                oos.writeObject("bBuy");
                oos.writeObject(p.toString() + "\n");
                System.out.println(p.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            ArrayList<String> newPurchases = new ArrayList<>();
            for (Product p : cart) {
                newPurchases.add(p.getName() + "," + p.getSeller() + "\n");
            }
            ArrayList<String> newAllPurchases = new ArrayList<>();
            for (Product p : cart) {
                newAllPurchases.add(p.getName() + "," + p.getSeller() + ","
                        + super.getEmail() + "," + p.getPrice() + "\n");
            }
            oos.writeObject((ArrayList<String>) newPurchases);
            oos.writeObject((ArrayList<String>) newAllPurchases);
            oos.writeObject(super.getEmail() + ".txt");
            oos.writeObject("AllPurchases.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearCart();
    }


    /**
     * This method prints out the choices the buyer can make when starting the program.
     *
     * @param buyer  The buyer
     * @param market The arraylist of stores that is the market
     * @param input  The scanner that is used for user input
     */
    public synchronized void choices(Buyers buyer, ArrayList<Store> market, Scanner input) {
        setupCart();
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        allProducts = getAllProducts(market);
        do {
            try {
                refresh();
                //System.out.println("Would you like to:");
                //System.out.println("\t1. View current listings");
                //System.out.println("\t2. Search for specific products");
                //System.out.println("\t3. Sort the market based on price/quantity");
                //System.out.println("\t4. Purchase items from product page");
                //System.out.println("\t5. View purchase history");
                //System.out.println("\t6. Checkout");
                //System.out.println("\t7. Edit cart");
                //System.out.println("\t8. Export purchase history");
                //System.out.println("\t9. View statistics");
                //System.out.println("\t10. Delete account");
                //System.out.println("\t11. Exit the application");
                do {
                    String[] choices = {"1. View current listings", "2. Search for specific products",
                            "3. Sort the market based on price/quantity",
                            "4. Purchase items from product page",
                            "5. View purchase history", "6. Checkout", "7. Edit cart",
                            "8. Export purchase history", "9. View statistics",
                            "10. Delete account",
                            "11. Exit the application"};
                    try {
                        String answer = (String) (JOptionPane.showInputDialog(null, "Buyer Options",
                                "Choice?", JOptionPane.QUESTION_MESSAGE,
                                null, choices, choices[0]));
                        int index = answer.indexOf(".");
                        choice = Integer.parseInt(answer.substring(0, index));
                        break;
                    } catch (InputMismatchException e) {
                        JOptionPane.showMessageDialog(null, "Please select a choice.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } while (true);
                switch (choice) {
                    case 1:
                        if (allProducts.size() == 0) {
                            //System.out.println("No products are currently listed.");
                            JOptionPane.showMessageDialog(null, "No products are currently listed.",
                                    "View Current Listings", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                        String[] products = new String[allProducts.size()];
                        int counter = 0;
                        for (Product p : allProducts) {
                            //System.out.println("Seller Name: " + p.getSeller());
                            //System.out.println(p.getListing());
                            //System.out.println();
                            String s = String.format("Name: %s | Description: %s | " +
                                            "Quantity: %d | Price: %.2f",
                                    p.getName(), p.getDesc(), p.getQuantity(), p.getPrice());
                            products[counter] = s;
                            counter++;
                        }
                        JOptionPane.showMessageDialog(null, products,
                                "View Products", JOptionPane.INFORMATION_MESSAGE);

                        //System.out.println("Would you like to see a specific product? (y/n)");
                        String[] iORe = new String[]{"Yes", "No"};
                        String ans = (String) JOptionPane.showInputDialog(null,
                                "Would you like to see a specific product? (y/n)",
                                "Search Product", JOptionPane.QUESTION_MESSAGE,
                                null, iORe, iORe[0]);


                        if (ans.equals("Yes")) {
                            boolean found = false;
                            //System.out.println("What is the name of the product you want to see?");
                            String productName = JOptionPane.showInputDialog(null,
                                    "What is the name of the product you want to see?", "Search Product",
                                    JOptionPane.INFORMATION_MESSAGE);
                            for (Store store : market) {
                                ArrayList<Product> p = store.getProducts();
                                for (Product pr : p) {
                                    if (pr.getName().equals(productName)) {
                                        //System.out.println(pr.getPage());
                                        JOptionPane.showMessageDialog(null, pr.getPage(),
                                                "Search Product", JOptionPane.INFORMATION_MESSAGE);
                                        found = true;
                                    }
                                }
                            }
                            if (found == false) {
                                //System.out.println("No product matching that name.");
                                JOptionPane.showMessageDialog(null, "No product matching that name.",
                                        "Search Product", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else if (ans.equals("No")) {

                        }
                        break;
                    case 2:
                        if (allProducts.size() == 0) {
                            //System.out.println("No products are currently listed.");
                            JOptionPane.showMessageDialog(null, "No products are currently listed.",
                                    "Search Product", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                        //System.out.println("What would you like to search?");
                        String search = JOptionPane.showInputDialog(null,
                                "What would you like to search?", "Search Product",
                                JOptionPane.INFORMATION_MESSAGE);
                        ;
                        ArrayList<Product> pro = new ArrayList<>();

                        boolean found = false;
                        for (Store store : market) {
                            int count = 0;
                            ArrayList<Product> p = store.getProducts();
                            for (Product pr : p) {
                                if (pr.getName().contains(search) || pr.getDesc().contains(search)) {
                                    //System.out.println(count++ + ": " + pr.getListing());
                                    JOptionPane.showMessageDialog(null, count++ + ": " + pr.getListing(),
                                            "Search Product", JOptionPane.INFORMATION_MESSAGE);
                                    pro.add(pr);
                                    found = true;
                                }
                            }
                        }
                        if (found == false) {
                            //System.out.println("No products matching that name.");
                            JOptionPane.showMessageDialog(null, "No products matching that name.",
                                    "Search Product", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    case 3: //Sort the market based on price/quantity
                        if (allProducts.size() == 0) {
                            //System.out.println("No products are currently listed.");
                            JOptionPane.showMessageDialog(null, "No products are currently listed.",
                                    "Sort Market", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                        String sortChoice = "";
                        do {
                            try {
                                //System.out.println("Sort on quantity or price? (1/2)");
                                String[] iORe2 = new String[]{"Quantity", "Price"};
                                sortChoice = (String) JOptionPane.showInputDialog(null,
                                        "Sort on quantity or price?", "Sort Market", JOptionPane.QUESTION_MESSAGE,
                                        null, iORe2, iORe2[0]);
                            } catch (InputMismatchException e) {
                                //System.out.println("Please enter either 1 or 2.");
                                JOptionPane.showMessageDialog(null, "Please select a choice.",
                                        "Sort Market", JOptionPane.ERROR_MESSAGE);
                            }
                            if (sortChoice.equals("Quantity")) {
                                //System.out.println("Sorting by quantity");
                                JOptionPane.showMessageDialog(null, "Sorting by quantity",
                                        "Sort Market", JOptionPane.INFORMATION_MESSAGE);
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

                            } else if (sortChoice.equals("Price")) {
                                //System.out.println("Sorting by price");
                                JOptionPane.showMessageDialog(null, "Sorting by price",
                                        "Sort Market", JOptionPane.INFORMATION_MESSAGE);
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
                                //System.out.println("Invalid Input");
                                JOptionPane.showMessageDialog(null, "Invalid Input",
                                        "Sort Market", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (sortChoice == null);

                        String[] products2 = new String[allProducts.size()];
                        int counter2 = 0;
                        for (Product p : allProducts) {
                            //System.out.println("Seller Name: " + p.getSeller());
                            //System.out.println(p.getListing());
                            //System.out.println();
                            String s = String.format("Name: %s | Description: %s | Quantity: %d | Price: %.2f",
                                    p.getName(), p.getDesc(), p.getQuantity(), p.getPrice());
                            products2[counter2] = s;
                            counter2++;
                        }
                        JOptionPane.showMessageDialog(null, products2,
                                "Sort Market", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case 4: // Purchase items from product page
                        if (allProducts.size() == 0) {
                            //System.out.println("No products are currently listed.");
                            JOptionPane.showMessageDialog(null, "No products are currently listed.",
                                    "Purchase Items", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                        //System.out.println("What item do you want to add to your cart?");
                        String item = JOptionPane.showInputDialog(null,
                                "What item do you want to add to your cart?", "Purchase Items",
                                JOptionPane.INFORMATION_MESSAGE);
                        ;
                        boolean find = false;
                        //for (Store m : market) {
                        for (Product p : allProducts) {
                            if (p.getName().equals(item)) {
                                int num = -1;
                                do {
                                    //System.out.println("How many do you want to add to your cart?");
                                    boolean loop = false;
                                    do {
                                        try {
                                            String number = JOptionPane.showInputDialog(null,
                                                    "How many do you want to add to your cart?", "Purchase Items",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            num = Integer.parseInt(number);
                                            loop = true;
                                        } catch (NumberFormatException e) {
                                            JOptionPane.showMessageDialog(null, "Please enter an integer.",
                                                    "Purchase Items", JOptionPane.ERROR_MESSAGE);
                                            loop = false;
                                        }
                                    } while (loop == false);

                                    if (num < 0 || num > p.getQuantity()) {
                                        //System.out.println("Error adding that many to cart.");
                                        JOptionPane.showMessageDialog(null, "Cannot add that many to cart.",
                                                "Purchase Items", JOptionPane.ERROR_MESSAGE);
                                        break;
                                    } else {
                                        for (int i = 0; i < num; i++) {
                                            addToCart(p);
                                        }
                                        //System.out.println("Added!");
                                        JOptionPane.showMessageDialog(null, "Added successfully!",
                                                "Purchase Items", JOptionPane.INFORMATION_MESSAGE);
                                    }

                                } while (num == -1);
                                find = true;
                            }
                        }
                        //}
                        if (!find) {
                            //System.out.println("No item matching that name!");
                            JOptionPane.showMessageDialog(null, "No item matching that name!",
                                    "Purchase Items", JOptionPane.ERROR_MESSAGE);
                        } else {
                            saveCart();
                        }
                        break;
                    case 5: // View Purchase History
                        ArrayList<String> history = null;
                        try {
                            oos.writeObject("bHistory");
                            //System.out.println("Purchase history:");
                            oos.writeObject(super.getEmail() + ".txt");
                            history = (ArrayList<String>) ois.readObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (history.size() == 0) {
                            //System.out.println("No purchased items.");
                            JOptionPane.showMessageDialog(null, "No purchased items.",
                                    "Purchase Items", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            String output = "Purchase history:\n";
                            for (String s : history) {
                                String name = s.substring(0, s.indexOf(','));
                                String seller = s.substring(s.indexOf(',') + 1);
                                //System.out.println("Purchase history:");
                                //System.out.println("\t" + name + " sold by " + seller);
                                output += (name + " sold by " + seller + "\n");
                            }
                            JOptionPane.showMessageDialog(null, output,
                                    "Purchase Items", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    case 6: // Checkout
                        if (shoppingCart.isEmpty()) {
                            //System.out.println("Nothing in your cart!");
                            JOptionPane.showMessageDialog(null, "Nothing in your cart!",
                                    "Checkout", JOptionPane.ERROR_MESSAGE);
                        } else {
                            //System.out.println("You are purchasing: ");
                            String output = "You are purchasing:\n";
                            for (Product p : shoppingCart) {
                                //System.out.println("Name: " + p.getName());
                                output += ("Name: " + p.getName() + "\n");
                            }
                            JOptionPane.showMessageDialog(null, output,
                                    "Checkout", JOptionPane.INFORMATION_MESSAGE);

                            //System.out.println("Proceed? (y/n)");
                            String[] iORe2 = new String[]{"Yes", "No"};
                            String answer = (String) JOptionPane.showInputDialog(null, "Proceed? (y/n)",
                                    "Checkout", JOptionPane.QUESTION_MESSAGE,
                                    null, iORe2, iORe2[0]);

                            if (answer.equals("Yes")) {
                                //System.out.println("Verify purchase with your password.");
                                String password = JOptionPane.showInputDialog(null,
                                        "Verify purchase with your password.", "Checkout",
                                        JOptionPane.INFORMATION_MESSAGE);
                                if (password.equals(super.getPassword())) {
                                    //System.out.println("Purchase completed!");
                                    JOptionPane.showMessageDialog(null, "Purchase completed!",
                                            "Checkout", JOptionPane.INFORMATION_MESSAGE);
                                    addToFile(shoppingCart);
                                    shoppingCart = new ArrayList<Product>();
                                    clearCart();
                                    saveCart();
                                } else {
                                    //System.out.println("Incorrect password, purchase cancelled.");
                                    JOptionPane.showMessageDialog(null, "Incorrect password, purchase cancelled.",
                                            "Checkout", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                //System.out.println("Purchase cancelled");
                                JOptionPane.showMessageDialog(null, "Purchase Canceled",
                                        "Checkout", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        break;
                    case 7: // Edit Cart
                        if (shoppingCart.isEmpty()) {
                            //System.out.println("Nothing in your cart!");
                            JOptionPane.showMessageDialog(null, "Nothing in your cart!",
                                    "Edit Cart", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        //System.out.println("Your cart has:");
                        String cartItems = "Your cart has:\n";
                        int count = 0;
                        for (Product p : shoppingCart) {
                            //System.out.println(count++ + ": " + p.getName());
                            cartItems += (count++ + ": " + p.getName() + "\n");
                        }
                        JOptionPane.showMessageDialog(null, cartItems,
                                "Edit Cart", JOptionPane.INFORMATION_MESSAGE);

                        //System.out.println("Would you like remove a product?(enter a number, or -1 to cancel)");
                        String[] iORe2 = new String[]{"Yes", "No"};
                        String answer = (String) JOptionPane.showInputDialog(null, "Would you like remove a product?",
                                "Edit Cart", JOptionPane.QUESTION_MESSAGE,
                                null, iORe2, iORe2[0]);
                        if (answer.equals("Yes")) {
                            boolean loop = false;
                            do {
                                try {
                                    String number = JOptionPane.showInputDialog(null,
                                            "Which item would you like to remove? Enter a number.", "Edit Cart",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    int num = Integer.parseInt(number);
                                    String output = "Removed " + shoppingCart.get(num).getName();
                                    JOptionPane.showMessageDialog(null, output,
                                            "Edit Cart", JOptionPane.INFORMATION_MESSAGE);
                                    shoppingCart.get(num).increaseQuantity();
                                    shoppingCart.remove(num);
                                    saveCart();
                                    loop = true;
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(null, "Please enter a valid integer.",
                                            "Edit Cart", JOptionPane.ERROR_MESSAGE);
                                    e.printStackTrace();
                                    loop = false;
                                }
                            } while (loop == false);
                        } else {
                            JOptionPane.showMessageDialog(null, "Cancelling...",
                                    "Edit Cart", JOptionPane.INFORMATION_MESSAGE);
                        }

                        /*if (input.hasNextInt()) {
                            count = input.nextInt();
                            input.nextLine();
                            if (count == -1) {
                                System.out.println("Cancelling...");
                            } else {
                                try {
                                    System.out.println("Removed " + shoppingCart.get(count).getName());
                                    shoppingCart.get(count).increaseQuantity();
                                    shoppingCart.remove(count);
                                    saveCart();
                                } catch (Exception e) {
                                    System.out.println("Please enter a valid integer.");
                                    count = -2;
                                }
                            }
                        } else {
                            input.nextLine();
                            System.out.println("Please enter an integer.");
                        }*/

                        break;
                    case 8: // Export Purchase History
                        export();
                        break;
                    case 9: // View Statistics
                        // keep for exit statement
                        statistics(input);
                        break;
                    case 10: // Delete Account
                        super.deleteAccount(ois, oos);
                        choice = 11;
                        break;
                    case 11: // Exit
                        saveCart();
                        try {
                            oos.writeObject("bExit");
                            oos.writeObject(market);
                            oos.writeObject("bClose");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Enter Valid Number", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        //System.out.println("Enter Valid Number");
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error, exiting the program...", "Do not exit", JOptionPane.ERROR_MESSAGE);

                saveCart();
                try {
                    oos.writeObject("bExit");
                    oos.writeObject(market);
                    oos.writeObject("bClose");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                choice = 11;
            }
        } while (choice != 11);
    }

    /**
     * This method exports purchase history to a given file.
     */
    private void export() {
        JOptionPane.showMessageDialog(null,
                "Your purchase history has been exported to the file " + super.getEmail() + ".txt",
                "Exported!", JOptionPane.INFORMATION_MESSAGE);
        //System.out.println("Your purchase history has been exported to the file " + super.getEmail() + ".txt");

    }

    /**
     * This method returns the arraylist of the products
     *
     * @param market The market
     * @return The market
     */
    public ArrayList<Product> getAllProducts(ArrayList<Store> market) {
        ArrayList<Product> allProductss = new ArrayList<>();
        for (Store store : market) {
            if (!store.isEmpty()) {
                ArrayList<Product> p = store.getProducts();
                for (Product product : p) {
                    allProductss.add(product);
                }
            }
        }
        return allProductss;
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
        ArrayList<String> sellerPurchases = new ArrayList<>();
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
            sellerPurchases.add(String.format("Seller %s has sold %d products\n", s, count));
            //System.out.printf("Seller %s has sold %d products\n", s, count);
        }
        JOptionPane.showMessageDialog(null, sellerPurchases, "Purchases", JOptionPane.INFORMATION_MESSAGE);
        ArrayList<String> yourPurchaseHistory = new ArrayList<>();
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
            yourPurchaseHistory.add(String.format("You have bought %d products from %s\n", count, s));
            //System.out.printf("You have bought %d products from %s\n", count, s);
        }
        JOptionPane.showMessageDialog(null, yourPurchaseHistory, "Purchases", JOptionPane.INFORMATION_MESSAGE);
        //System.out.println("Would you like to sort?(y/n)");
        String answer = JOptionPane.showInputDialog(null, "Would you like to sort?(y/n)",
                "Sort?", JOptionPane.QUESTION_MESSAGE);
        if (answer.equalsIgnoreCase("y")) {
            Collections.sort(allPurchases, reverseOrder());
            Collections.sort(yourPurchases, reverseOrder());
            ArrayList<String> sellerStuff = new ArrayList<>();
            for (String str : allPurchases) {
                String numBought = str.substring(0, str.indexOf(','));
                String seller = str.substring(str.indexOf(',') + 1);
                sellerStuff.add(String.format("Seller %s has sold %s products\n", seller, numBought));
                //System.out.printf("Seller %s has sold %s products\n", seller, numBought);
            }
            JOptionPane.showMessageDialog(null, sellerStuff, "Seller history", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("-----------------------");
            ArrayList<String> boughtStuff = new ArrayList<>();
            for (String string : yourPurchases) {
                String numBought = string.substring(0, string.indexOf(','));
                String seller = string.substring(string.indexOf(',') + 1);
                boughtStuff.add(String.format("You have bought %s products from %s\n", numBought, seller));
                //System.out.printf("You have bought %s products from %s\n", numBought, seller);
            }
            JOptionPane.showMessageDialog(null, boughtStuff, "history", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public synchronized void refresh() {
        try {
            allProducts.clear();
            oos.writeObject("bRefresh");
            while (true) {
                String s = (String) ois.readObject();
                if (s == null)
                    break;
                allProducts.add(new Product(s));
            }
            oos.writeObject("close");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
