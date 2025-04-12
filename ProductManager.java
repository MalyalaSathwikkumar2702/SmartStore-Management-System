package store;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProductAt(int index) {
        if (index >= 0 && index < products.size()) {
            products.remove(index); // ✅ Remove from list
        }
    }

    // ✅ Get a product by index — needed for editing
    public Product getProductAt(int index) {
        if (index >= 0 && index < products.size()) {
            return products.get(index);
        }
        return null;
    }

    public String displayProducts() {
        StringBuilder sb = new StringBuilder();
        for (Product p : products) {
            sb.append("Name: ").append(p.getName())
              .append(", Price: ").append(p.getPrice())
              .append(", Quantity: ").append(p.getQuantity())
              .append("\n");
        }
        return sb.toString();
    }
    
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Product p : products) {
                writer.write(p.getName() + "," + p.getPrice() + "," + p.getQuantity());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Load products from file
    public void loadFromFile(String filename) {
        products.clear(); // clear old data
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    products.add(new Product(name, price, quantity));
                }
            }
        } catch (IOException e) {
            System.out.println("No saved products found. Starting fresh.");
        }
    }

    // ✅ Return the full product list
    public List<Product> getAllProducts() {
        return products;
    }
}
