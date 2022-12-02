import java.util.*;
import java.io.*;
/**
 * @author Mao, Chakrabarty, Lee, Johnson, Muthyala
 * @version 11.13.22
 */
public class Store implements Serializable {
    private ArrayList<Product> products;
    private String sellerName;

    private String fileName;

    public Store(String sellerName) {
        this.sellerName = sellerName;
        fileName = sellerName + ".txt";
        products = new ArrayList<>();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public boolean isEmpty() {
        return (products.size() == 0);
    }

    /**
     * This method adds a product to the store.
     * @param p The product to add.
     */
    public void addProduct(Product p) {
        boolean duplicate = false;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).equals(p)) {
                duplicate = true;
            }
        }
        if (!duplicate)
            products.add(p);
        else {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).equals(p)) {
                    products.get(i).increaseQuantity();
                }
            }
        }
    }

    /**
     * This method prints the store to a given file, to save after logout. The file is the set filename.
     */
    public void printToFile() {
        File f = new File(fileName);
        try {
            PrintWriter pw = new PrintWriter(f);
            for (int i = 0; i < products.size(); i++) {
                pw.write(products.get(i).toString() + "\n");
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method prints the given store to a given file.
     * @param f The file to print the store to.
     */
    public void printToFile(File  f) {
        try {
            FileWriter fw = new FileWriter(f);
            for (int i = 0; i < products.size(); i++) {
                fw.write(products.get(i).toString() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String s = "Seller name: " + sellerName + "\n";
        for (int i = 0; i < products.size(); i++) {
            s += products.get(i).toString() + "\n";
        }
        return s;
    }
}