package com.example.possystem.store;

import com.example.possystem.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InventoryStore {
    private static final InventoryStore INSTANCE = new InventoryStore();

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private double totalSalesToday = 0.0;
    private int completedOrdersToday = 0;

    private InventoryStore() {
        products.addAll(
            new Product("COF-01", "Espresso Beans (250g)", "Beverages", 12.50, 24),
            new Product("COF-02", "Matcha Latte Powder", "Beverages", 16.00, 6),
            new Product("BAK-01", "Butter Croissant", "Bakery", 3.75, 18),
            new Product("BAK-02", "Sourdough Loaf", "Bakery", 7.50, 4),
            new Product("ACC-01", "Ceramic Pour-Over Dripper", "Equipment", 28.00, 10),
            new Product("ACC-02", "Double-Wall Glass Mug", "Equipment", 14.00, 3)
        );
    }

    public static InventoryStore getInstance() {
        return INSTANCE;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public void recordSale(double amount) {
        totalSalesToday += amount;
        completedOrdersToday++;
    }

    public double getTotalSalesToday() {
        return totalSalesToday;
    }

    public int getCompletedOrdersToday() {
        return completedOrdersToday;
    }
}