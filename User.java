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
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;

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

    public static void loadUsers() throws IOException {
        oos.writeObject("load");
        try {
            allUsers = (ArrayList<String>) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static User prompt(ObjectInputStream uois, ObjectOutputStream uoos) {

        ois = uois;
        oos = uoos;

        System.out.println("Welcome to the Car Marketplace");
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        try {
            loadUsers();
        } catch (IOException e) {
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
                try {
                    return signUp();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public static User signUp() throws IOException {
        oos.writeObject("signup");

        Scanner scanner = new Scanner(System.in);

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

        oos.writeObject(attemptedEmail);
        oos.writeObject(attemptedPassword);
        oos.writeObject(buyerOrSeller);

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

    public void deleteAccount() throws IOException {
        oos.writeObject("delete");

        ArrayList<String> accounts = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            String s = accounts.get(i);
            if (s.substring(0, s.indexOf(',')).equals(getEmail())) {
                accounts.remove(i);
            }
        }

        oos.writeObject(accounts);
    }

    public String toString() {
        String s = String.format("Email: %s | Password: %s | Buyer Or Seller: %s",
                getEmail(), getPassword(), getBuyOrSell());
        return s;
    }
}