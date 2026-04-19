package models;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String category;
    private String imagePath;

    public Product() {}

    public Product(int id, String name, String description, double price, int stock, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // Getters
    public int getId()            { return id; }
    public String getName()       { return name; }
    public String getDescription(){ return description; }
    public double getPrice()      { return price; }
    public int getStock()         { return stock; }
    public String getCategory()   { return category; }
    public String getImagePath()  { return imagePath; }

    // Setters
    public void setId(int id)                  { this.id = id; }
    public void setName(String name)           { this.name = name; }
    public void setDescription(String desc)    { this.description = desc; }
    public void setPrice(double price)         { this.price = price; }
    public void setStock(int stock)            { this.stock = stock; }
    public void setCategory(String category)   { this.category = category; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price);
    }
}
