package com.example.possystem.controller;

import com.example.possystem.model.Product;
import com.example.possystem.store.InventoryStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class InventoryController {

    @FXML private TextField skuField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField restockAmountField;

    @FXML private TableView<Product> inventoryTable;
    @FXML private TableColumn<Product, String> skuCol;
    @FXML private TableColumn<Product, String> nameCol;
    @FXML private TableColumn<Product, String> categoryCol;
    @FXML private TableColumn<Product, String> priceCol;
    @FXML private TableColumn<Product, Integer> stockCol;
    @FXML private TableColumn<Product, String> alertCol;

    @FXML private Label lowStockLabel;

    private final InventoryStore store = InventoryStore.getInstance();

    @FXML
    public void initialize() {
        categoryBox.setItems(FXCollections.observableArrayList("Beverages", "Bakery", "Equipment", "General"));
        categoryBox.setValue("General");

        skuCol.setCellValueFactory(d -> d.getValue().skuProperty());
        nameCol.setCellValueFactory(d -> d.getValue().nameProperty());
        categoryCol.setCellValueFactory(d -> d.getValue().categoryProperty());
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getPrice())));
        stockCol.setCellValueFactory(d -> d.getValue().stockProperty().asObject());
        alertCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStock() <= 5 ? "LOW STOCK" : "OK"));

        alertCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("badge-low", "badge-ok");
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    getStyleClass().add("LOW STOCK".equals(item) ? "badge-low" : "badge-ok");
                }
            }
        });

        inventoryTable.setItems(store.getProducts());
        updateLowStockSummary();
    }

    @FXML
    private void handleAddProduct() {
        String sku = skuField.getText().trim().toUpperCase();
        String name = nameField.getText().trim();
        String category = categoryBox.getValue();

        if (sku.isEmpty() || name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "SKU and Product Name are required.");
            return;
        }

        boolean duplicateSku = store.getProducts().stream().anyMatch(p -> p.getSku().equalsIgnoreCase(sku));
        if (duplicateSku) {
            showAlert(Alert.AlertType.ERROR, "Duplicate SKU", "A product with SKU " + sku + " already exists.");
            return;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            if (price < 0 || stock < 0) {
                throw new NumberFormatException();
            }

            store.getProducts().add(new Product(sku, name, category, price, stock));
            skuField.clear();
            nameField.clear();
            priceField.clear();
            stockField.clear();
            updateLowStockSummary();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Numbers", "Enter a valid non-negative price and whole-number stock.");
        }
    }

    @FXML
    private void handleRestock() {
        Product selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a product to restock.");
            return;
        }

        try {
            int addQty = Integer.parseInt(restockAmountField.getText().trim());
            if (addQty <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Restock amount must be greater than 0.");
                return;
            }
            selected.setStock(selected.getStock() + addQty);
            restockAmountField.clear();
            inventoryTable.refresh();
            updateLowStockSummary();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Enter a valid number to add to stock.");
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            store.getProducts().remove(selected);
            updateLowStockSummary();
        }
    }

    private void updateLowStockSummary() {
        long lowCount = store.getProducts().stream().filter(p -> p.getStock() <= 5).count();
        lowStockLabel.setText("Low Stock Alerts (<= 5 units): " + lowCount);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}