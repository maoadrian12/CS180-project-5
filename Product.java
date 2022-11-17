/**
 * @author Mao, Chakrabarty, Lee, Johnson, Muthyala
 * @version 11.13.22
 */
public class Product {

    private String name;
    private String seller;
    private String desc;
    private int quantity;
    private double price;

    public Product(String name, String seller, String desc, int quantity, double price) {
        this.name = name;
        this.seller = seller;
        this.desc = desc;
        this.quantity = quantity;
        this.price = price;
    }

    public Product(String info) {
        String s = info;
        String nameProduct = s.substring(0, s.indexOf(','));
        s = s.substring(s.indexOf(',') + 1);
        String sellerProduct = s.substring(0, s.indexOf(','));
        s = s.substring(s.indexOf(',') + 1);
        String descProduct = s.substring(0, s.indexOf(','));
        s = s.substring(s.indexOf(',') + 1);
        int quantityProduct = Integer.parseInt(s.substring(0, s.indexOf(',')));
        s = s.substring(s.indexOf(',') + 1);
        Double d = Double.parseDouble(s);
        this.name = nameProduct;
        this.seller = sellerProduct;
        this.desc = descProduct;
        this.quantity = quantityProduct;
        this.price = d;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    @Override
    public String toString() {
        String s = String.format("%s,%s,%s,%d,%.2f",
                name, seller, desc, quantity, price);
        return s;
    }

    @Override
    public boolean equals(Object o) {
        boolean equal = false;
        if (o instanceof Product) {
            Product p = (Product) o;
            if (p.toString().equals(this.toString())) {
                equal = true;
            }
        }
        return equal;
    }
    public void increaseQuantity() {
        quantity++;
    }

    public void increaseQuantity(int increase) {
        quantity += increase;
    }

    /**
     * The listing for the product(simplified)
     * @return The string that contains their listing
     */
    public String getListing() {
        return ("Product name: " + name + "\nPrice: " + price + "\nQuantity: " + quantity);
    }

    /**
     * The detailed listing for the product
     * @return The string that contains the detailed listing
     */
    public String getPage() {
        String s = String.format("Description: %s\nQuantity Available: %d", desc, quantity);
        return s;
    }

    public void decreaseQuantity() {
        quantity--;
    }
}