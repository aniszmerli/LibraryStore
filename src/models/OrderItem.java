package models;

public class OrderItem {
    private int id;
    private Product product;
    private int quantity;
    private double unitPrice;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public int getId()             { return id; }
    public Product getProduct()    { return product; }
    public int getQuantity()       { return quantity; }
    public double getUnitPrice()   { return unitPrice; }
    public double getSubtotal()    { return unitPrice * quantity; }

    public void setId(int id)           { this.id = id; }
    public void setQuantity(int qty)    { this.quantity = qty; }
    public void setUnitPrice(double p)  { this.unitPrice = p; }
}
