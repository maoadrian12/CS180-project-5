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

    public static void loadUsers(ObjectInputStream ois, ObjectOutputStream oos) throws IOException, ClassNotFoundException {
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
        System.out.println("Welcome to the Car Marketplace");
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        String[] userDetails = new String[2];

        try {
            loadUsers(ois, oos);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        do {
            System.out.println("1. Log In\n2. Sign Up");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                choice = -1;
                scanner.nextLine();
            }
            if (choice == 1) {
                return logIn();
            } else if (choice == 2) {
                return signUp(ois, oos);
            } else {
                System.out.println("Invalid Choice");
            }
        } while (choice != 1 && choice != 2);
        return new User();
    }

    public static User logIn() {
        Scanner scanner = new Scanner(System.in);
        boolean existingUser = false;
        boolean incorrectPassword = false;

        String loginEmail = "";
        String loginPassword = "";
        String buyerOrSeller = "";
        int tryAgain = -1;

        System.out.println("Log In\n----------------");
        do {
            existingUser = false;
            incorrectPassword = false;

            System.out.print("Email: ");
            loginEmail = scanner.nextLine();

            for (String userString : allUsers) {
                String[] userArr = userString.split(",");

                if (loginEmail.equals(userArr[0])) {
                    System.out.print("Password: ");
                    loginPassword = scanner.nextLine();

                    if (loginPassword.equals(userArr[1])) {
                        existingUser = true;
                        buyerOrSeller = userArr[2];
                    } else {
                        incorrectPassword = true;
                        System.out.println("Password Incorrect");
                    }
                }
            }

            if (!existingUser && !incorrectPassword) {
                System.out.println("No User Exists With That Email");
            }

            if (!existingUser) {
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
            }
        } while (!existingUser && tryAgain == 1);

        if (buyerOrSeller.equals("buyer")) {
            return new Buyers(loginEmail, loginPassword);
        } else if (buyerOrSeller.equals("seller")) {
            return new Sellers(loginEmail, loginPassword);
        } else {
            return new User();
        }
    }

    public static User signUp(ObjectInputStream ois, ObjectOutputStream oos) {
        Scanner scanner = new Scanner(System.in);
        //File f = new File("UserAccounts.txt");

        String attemptedEmail = "";
        System.out.println("Sign Up\n----------------");
        do {
            do {
                System.out.print("Email: ");
                attemptedEmail = scanner.nextLine();
                if (attemptedEmail.isBlank() || attemptedEmail.isEmpty()) {
                    System.out.println("Please enter an email.");
                }
                if (attemptedEmail.contains(",")) {
                    System.out.println("Username cannot contain comma");
                }
            } while (attemptedEmail.contains(",") || attemptedEmail.length() == 0);

            for (String userString : allUsers) {
                String email = userString.substring(0, userString.indexOf(","));
                if (attemptedEmail.equals(email)) {
                    System.out.println("Email Already Taken");
                    attemptedEmail = "";
                }
            }
        } while (attemptedEmail.equals(""));

        String attemptedPassword;

        do {
            System.out.print("Password: ");
            attemptedPassword = scanner.nextLine();

            if (attemptedPassword.length() == 0) {
                System.out.println("Please enter a password.");
            }
            if (attemptedPassword.contains(",")) {
                System.out.println("Password cannot contain comma");
            }
        } while (attemptedPassword.contains(",") || attemptedPassword.length() == 0);

        String buyerOrSeller = "";
        do {
            System.out.print("Buyer or Seller?: ");
            buyerOrSeller = scanner.nextLine();

            if (!(buyerOrSeller.equals("buyer")) && !(buyerOrSeller.equals("seller"))) {
                System.out.println("Please input either \"buyer\" or \"seller\"");
            }

        } while (!(buyerOrSeller.equals("buyer")) && !(buyerOrSeller.equals("seller")));

        ArrayList<String> thisShouldNotBeAnArrayList = new ArrayList<>();
        thisShouldNotBeAnArrayList.add(String.format("%s,%s,%s", attemptedEmail, attemptedPassword, buyerOrSeller));
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
                }
            }
            for (String account : accounts) {
                oos.writeObject(account);
                System.out.println("Account deleted, terminating program...");
            }
            oos.writeObject(null);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error deleting account, try closing and rerunning the program.");
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