package store.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import store.Product;
import store.ProductManager;

public class StoreUI extends Application {
    private ProductManager manager = new ProductManager();
    private TextArea displayArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        // Input fields
        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField quantityField = new TextField();

        // Buttons
        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            manager.addProduct(new Product(name, price, quantity));
            displayArea.setText(manager.displayProducts());

            nameField.clear();
            priceField.clear();
            quantityField.clear();
        });

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        layout.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Price:"), priceField,
                new Label("Quantity:"), quantityField,
                addButton,
                new Label("Inventory:"),
                displayArea
        );

        Scene scene = new Scene(layout, 300, 400);
        primaryStage.setTitle("Store Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
