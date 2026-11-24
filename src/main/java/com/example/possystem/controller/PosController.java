package com.example.possystem.controller;

import com.example.possystem.model.CartItem;
import com.example.possystem.model.Product;
import com.example.possystem.store.InventoryStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class PosController {

    private static final double TAX_RATE = 0.10;

    @FXML private TextField searchField;
    @FXML private TableView<Product> catalogTable;
    @FXML private TableColumn<Product, String> catSkuCol;
    @FXML private TableColumn<Product, String> catNameCol;
    @FXML private TableColumn<Product, String> catPriceCol;
    @FXML private TableColumn<Product, Integer> catStockCol;

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> cartItemCol;
    @FXML private TableColumn<CartItem, Integer> cartQtyCol;
    @FXML private TableColumn<CartItem, String> cartTotalCol;

    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Label dailyStatsLabel;

    private final InventoryStore store = InventoryStore.getInstance();
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        catSkuCol.setCellValueFactory(d -> d.getValue().skuProperty());
        catNameCol.setCellValueFactory(d -> d.getValue().nameProperty());
        catPriceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getPrice())));
        catStockCol.setCellValueFactory(d -> d.getValue().stockProperty().asObject());

        cartItemCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        cartQtyCol.setCellValueFactory(d -> d.getValue().quantityProperty().asObject());
        cartTotalCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getLineTotal())));

        FilteredList<Product> filteredCatalog = new FilteredList<>(store.getProducts(), p -> true);
        searchField.textProperty().addListener((obs, oldVal, query) -> {
            String q = query == null ? "" : query.toLowerCase().trim();
            filteredCatalog.setPredicate(p -> q.isEmpty()
                    || p.getSku().toLowerCase().contains(q)
                    || p.getName().toLowerCase().contains(q)
                    || p.getCategory().toLowerCase().contains(q));
        });

        catalogTable.setItems(filteredCatalog);
        cartTable.setItems(cartItems);

        // Double-click catalog row to add to cart
        catalogTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleAddToCart();
            }
        });

        recalculateTotals();
    }

    @FXML
    private void handleAddToCart() {
        Product selected = catalogTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Product Selected", "Choose a product from the catalog first.");
            return;
        }

        Optional<CartItem> existing = cartItems.stream()
                .filter(c -> c.getSku().equals(selected.getSku()))
                .findFirst();

        int currentInCart = existing.map(CartItem::getQuantity).orElse(0);
        if (currentInCart + 1 > selected.getStock()) {
            showAlert(Alert.AlertType.WARNING, "Insufficient Stock", "Only " + selected.getStock() + " unit(s) available for " + selected.getName() + ".");
            return;
        }

        if (existing.isPresent()) {
            existing.get().setQuantity(currentInCart + 1);
            cartTable.refresh();
        } else {
            cartItems.add(new CartItem(selected, 1));
        }
        recalculateTotals();
    }

    @FXML
    private void handleDecreaseQty() {
        CartItem item = cartTable.getSelectionModel().getSelectedItem();
        if (item == null) return;

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartTable.refresh();
        } else {
            cartItems.remove(item);
        }
        recalculateTotals();
    }

    @FXML
    private void handleRemoveCartItem() {
        CartItem item = cartTable.getSelectionModel().getSelectedItem();
        if (item != null) {
            cartItems.remove(item);
            recalculateTotals();
        }
    }

    @FXML
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Empty Cart", "Add at least one item before checking out.");
            return;
        }

        // Verify stock one last time before committing
        for (CartItem item : cartItems) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                showAlert(Alert.AlertType.ERROR, "Stock Conflict", "Not enough stock for " + item.getName() + ".");
                return;
            }
        }

        double subtotal = calculateSubtotal();
        double tax = subtotal * TAX_RATE;
        double grandTotal = subtotal + tax;

        for (CartItem item : cartItems) {
            Product p = item.getProduct();
            p.setStock(p.getStock() - item.getQuantity());
        }

        store.recordSale(grandTotal);
        cartItems.clear();
        catalogTable.refresh();
        recalculateTotals();

        showAlert(Alert.AlertType.INFORMATION, "Sale Completed",
                String.format("Total Charged: $%.2f (includes $%.2f tax)", grandTotal, tax));
    }

    private double calculateSubtotal() {
        return cartItems.stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    private void recalculateTotals() {
        double subtotal = calculateSubtotal();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));
        dailyStatsLabel.setText(String.format("Today's Sales: $%.2f  (%d orders)",
                store.getTotalSalesToday(), store.getCompletedOrdersToday()));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}