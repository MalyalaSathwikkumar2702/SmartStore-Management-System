package store;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import java.text.NumberFormat;
import java.io.*;
import java.util.List;

public class MainApp extends Application {

    ProductManager productManager = new ProductManager();
    ListView<String> listView = new ListView<>();
    private final String FILE_NAME = "products.txt";
    
    private final String ADMIN_USERNAME = "admin";  // Hardcoded admin username
    private final String ADMIN_PASSWORD = "password";  // Hardcoded admin password

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Show Login Screen First
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage primaryStage) {
        // Create a login layout
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                showMainApp(primaryStage);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        VBox loginBox = new VBox(10, new Label("Admin Login"), usernameField, passwordField, loginButton);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.CENTER);

        Scene loginScene = new Scene(loginBox, 300, 200);
        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void showMainApp(Stage primaryStage) {
        // Once logged in, proceed to the main app
        NumberFormat formatter = NumberFormat.getNumberInstance(); // Formatter for commas

        // Load products when app starts
        productManager.loadFromFile(FILE_NAME);

        // Show existing products
        for (Product p : productManager.getAllProducts()) {
            String formattedPrice = formatter.format(p.getPrice());
            listView.getItems().add(p.getName() + " - ₹" + formattedPrice + " - Qty: " + p.getQuantity());
        }

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        // Add Button
        Button addButton = new Button("Add Product");

        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().replace(",", "").trim();
            String quantityText = quantityField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                int quantity = Integer.parseInt(quantityText);

                Product product = new Product(name, price, quantity);
                productManager.addProduct(product);
                productManager.saveToFile(FILE_NAME);

                String formattedPrice = formatter.format(price);
                listView.getItems().add(name + " - ₹" + formattedPrice + " - Qty: " + quantity);

                nameField.clear();
                priceField.clear();
                quantityField.clear();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Enter valid numbers for Price and Quantity!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Delete Button
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                productManager.removeProductAt(selectedIndex);
                listView.getItems().remove(selectedIndex);
                productManager.saveToFile(FILE_NAME);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a product to delete.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Edit Button
        Button editButton = new Button("Edit Selected");
        editButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                // Fetch existing product
                Product existing = productManager.getProductAt(selectedIndex);

                // Populate fields
                nameField.setText(existing.getName());
                priceField.setText(String.valueOf((int) existing.getPrice())); // integer style price
                quantityField.setText(String.valueOf(existing.getQuantity()));

                // Remove old product
                productManager.removeProductAt(selectedIndex);
                listView.getItems().remove(selectedIndex);
                productManager.saveToFile(FILE_NAME);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a product to edit.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            listView.getItems().clear();
            for (Product p : productManager.getAllProducts()) {
                if (p.getName().toLowerCase().contains(newValue.toLowerCase())) {
                    String formattedPrice = formatter.format(p.getPrice());
                    listView.getItems().add(p.getName() + " - ₹" + formattedPrice + " - Qty: " + p.getQuantity());
                }
            }
        });

        // Export CSV Button
        Button exportButton = new Button("Export to CSV");
        exportButton.setOnAction(e -> {
            productManager.saveToFile("products_export.csv");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Products exported to CSV successfully!", ButtonType.OK);
            alert.showAndWait();
        });

        // Analytics Button
        Button analyticsButton = new Button("Show Analytics");
        analyticsButton.setOnAction(e -> {
            int totalProducts = productManager.getAllProducts().size();
            int totalQuantity = 0;
            double totalValue = 0.0;

            for (Product p : productManager.getAllProducts()) {
                totalQuantity += p.getQuantity();
                totalValue += p.getPrice() * p.getQuantity();
            }

            // Display the analytics in an alert box
            String analyticsMessage = "Total Products: " + totalProducts + "\n"
                    + "Total Quantity: " + totalQuantity + "\n"
                    + "Total Value: ₹" + formatter.format(totalValue);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, analyticsMessage, ButtonType.OK);
            alert.setTitle("Store Analytics");
            alert.showAndWait();
        });

        // Layout
        VBox inputBox = new VBox(10, nameField, priceField, quantityField, addButton, deleteButton, editButton, exportButton, analyticsButton, searchField);
        inputBox.setPadding(new Insets(10));

        VBox listBox = new VBox(10, new Label("Products:"), listView);
        listBox.setPadding(new Insets(10));

        HBox root = new HBox(20, inputBox, listBox);

        Scene scene = new Scene(root, 600, 350);
        primaryStage.setTitle("Store Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
