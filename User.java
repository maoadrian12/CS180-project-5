import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Mao, Chakrabarty, Lee, Johnson, Muthyala
 * @version 11.13.22
 */
public class User {
    private static ArrayList<String> allUsers = new ArrayList<>();
    private String email;
    private String password;
    private String buyOrSell;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {
        this.email = "";
        this.password = "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBuyOrSell() {
        return buyOrSell;
    }

    public void setBuyOrSell(String buyOrSell) {
        this.buyOrSell = buyOrSell;
    }

    public static ArrayList<String> getAllUsers() {
        return allUsers;
    }

    public static void loadUsers(ObjectInputStream ois, ObjectOutputStream oos)
            throws IOException, ClassNotFoundException {
        oos.writeObject("uLoad");
        ArrayList<String> users = (ArrayList<String>) ois.readObject();
        for (String s : users) {
            allUsers.add(s);
        }
        /*File f = new File("UserAccounts.txt");
        f.createNewFile();
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        String s;
        while ((s = bfr.readLine()) != null)
            allUsers.add(s);*/
    }

    public static User prompt(ObjectInputStream ois, ObjectOutputStream oos) {
        JOptionPane.showMessageDialog(null, "Welcome to the Car Marketplace", "Welcome!",
                JOptionPane.INFORMATION_MESSAGE);
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        String[] userDetails = new String[2];

        try {
            loadUsers(ois, oos);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        do {
            try {
                String[] options = {"Log In", "Sign Up"};
                choice = 1 + JOptionPane.showOptionDialog(null,
                        "Welcome to the Online Car Marketplace!", "Welcome",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, options, options[1]);
                //choice = Integer.parseInt(JOptionPane.showInputDialog(null,
                // "Welcome to the Online Car Marketplace!\n1. Log In\n2. Sign Up",
                // "Welcome", JOptionPane.QUESTION_MESSAGE));
            } catch (InputMismatchException e) {
                choice = -1;
            }
            if (choice == 1) {
                return logIn();
            } else if (choice == 2) {
                return signUp(ois, oos);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Answer", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while (choice != 1 && choice != 2);
        return new User();
    }

    public static User logIn() {
        try {
            Scanner scanner = new Scanner(System.in);
            boolean existingUser = false;
            boolean incorrectPassword = false;

            String loginEmail = "";
            String loginPassword = "";
            String buyerOrSeller = "";
            int tryAgain = -1;
            do {
                existingUser = false;
                incorrectPassword = false;

                loginEmail = JOptionPane.showInputDialog(null, "Email:", "Log In",
                        JOptionPane.INFORMATION_MESSAGE);

                for (String userString : allUsers) {
                    String[] userArr = userString.split(",");

                    if (loginEmail.equals(userArr[0])) {
                        loginPassword = JOptionPane.showInputDialog(null, "Password:", "Log In",
                                JOptionPane.INFORMATION_MESSAGE);
                        if (loginPassword.equals(userArr[1])) {
                            existingUser = true;
                            buyerOrSeller = userArr[2];
                        } else {
                            incorrectPassword = true;
                            JOptionPane.showMessageDialog(null, "Incorrect Password!", "Log In",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

                if (!existingUser && !incorrectPassword) {
                    JOptionPane.showMessageDialog(null, "No User Exists With That Email",
                            "Log In", JOptionPane.ERROR_MESSAGE);
                }

            /*if (!existingUser) {
                do {
                    try {
                        System.out.println("Attempt Login Again?");
                        System.out.println("1. Yes\n2. No");
                        tryAgain = scanner.nextInt();
                        scanner.nextLine();
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Please enter an integer.");
                        scanner.nextLine();
                    }
                } while (true);
            }*/
            } while (!existingUser && tryAgain == 1);

            if (buyerOrSeller.equals("buyer")) {
                return new Buyers(loginEmail, loginPassword);
            } else if (buyerOrSeller.equals("seller")) {
                return new Sellers(loginEmail, loginPassword);
            } else {
                return new User();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Exiting program...", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return null;
    }

    public static User signUp(ObjectInputStream ois, ObjectOutputStream oos) {
        Scanner scanner = new Scanner(System.in);
        //File f = new File("UserAccounts.txt");

        String attemptedEmail = "";
        do {
            do {
                attemptedEmail = JOptionPane.showInputDialog(null, "Email:",
                        "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                try {
                    if (attemptedEmail.isBlank() || attemptedEmail.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter an email", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    if (attemptedEmail.contains(",")) {
                        JOptionPane.showMessageDialog(null,
                                "Username cannot contain comma", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            "Component exited, quitting program.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            } while (attemptedEmail.contains(",") || attemptedEmail.length() == 0);

            for (String userString : allUsers) {
                String email = userString.substring(0, userString.indexOf(","));
                if (attemptedEmail.equals(email)) {
                    JOptionPane.showMessageDialog(null, "Email Already Taken", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    attemptedEmail = "";
                }
            }
        } while (attemptedEmail.equals(""));

        String attemptedPassword;

        do {
            attemptedPassword = JOptionPane.showInputDialog(null, "Password:",
                    "Sign Up", JOptionPane.INFORMATION_MESSAGE);

            if (attemptedPassword.length() == 0) {
                JOptionPane.showMessageDialog(null, "Please enter a password", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            if (attemptedPassword.contains(",")) {
                JOptionPane.showMessageDialog(null, "Password cannot contain comma", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while (attemptedPassword.contains(",") || attemptedPassword.length() == 0);


        int buyerOrSellerNum = 0;
        String buyerOrSeller = "";
        do {
            String[] options = {"Buyer", "Seller"};
            buyerOrSellerNum = JOptionPane.showOptionDialog(null, "Buyer or Seller?:",
                    "Sign Up", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (buyerOrSellerNum == 0)
                buyerOrSeller = "buyer";
            if (buyerOrSellerNum == 1)
                buyerOrSeller = "seller";


        } while (!(buyerOrSeller.equals("buyer")) && !(buyerOrSeller.equals("seller")));
        ArrayList<String> thisShouldNotBeAnArrayList = new ArrayList<>();
        thisShouldNotBeAnArrayList.add(String.format("%s,%s,%s", attemptedEmail, attemptedPassword,
                buyerOrSeller));
        try {
            oos.writeObject("uSignup");
            oos.writeObject(thisShouldNotBeAnArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
            f.createNewFile();
            String s = String.format("%s,%s,%s", attemptedEmail, attemptedPassword, buyerOrSeller);
            pw.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        String[] userData = new String[]{attemptedEmail, attemptedPassword, buyerOrSeller};

        if (userData[2].equals("seller")) {
            Sellers seller = new Sellers(userData[0], userData[1]);
            return seller;
        } else if (userData[2].equals("buyer")) {
            Buyers buyer = new Buyers(userData[0], userData[1]);
            return buyer;
        } else {
            return new User();
        }
    }

    public void deleteAccount(ObjectInputStream ois, ObjectOutputStream oos) {
        ArrayList<String> accounts = null;
        try {
            oos.writeObject("uDelete");
            accounts = (ArrayList<String>) ois.readObject();
            for (int i = 0; i < accounts.size(); i++) {
                String s = accounts.get(i);
                if (s.substring(0, s.indexOf(',')).equals(getEmail())) {
                    accounts.remove(i);
                    JOptionPane.showMessageDialog(null,
                            "Account deleted, terminating program...", "Account",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            for (String account : accounts) {
                oos.writeObject(account);
            }
            oos.writeObject(null);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error deleting account, try closing and rerunning the program.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        /*File f = new File("UserAccounts.txt");
        if (!f.exists() || f.isDirectory()) {
            System.out.println("Error deleting account, try closing and rerunning the program.");
        } else {
            try {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                while (true) {
                    String s = br.readLine();
                    if (s == null) {
                        break;
                    }
                    accounts.add(s);
                }
            } catch (IOException e) {
                System.out.println("Error deleting account, try closing and rerunning the program.");
            }
        }*/

        /*try {
            FileWriter fw = new FileWriter(f);
            for (String s : accounts) {
                fw.write(s + "\n");
            }
            fw.close();
            System.out.println("Account deleted, terminating program...");
        } catch (IOException e) {
            System.out.println("Error deleting account, try closing and rerunning the program.");
        }
        */
    }

    public String toString() {
        String s = String.format("Email: %s | Password: %s | Buyer Or Seller: %s",
                getEmail(), getPassword(), getBuyOrSell());
        return s;
    }
}