package com.example.possystem.model;

import javafx.beans.property.*;

public class CartItem {
    private final Product product;
    private final IntegerProperty quantity;
    private final DoubleProperty lineTotal;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = new SimpleIntegerProperty(quantity);
        this.lineTotal = new SimpleDoubleProperty(product.getPrice() * quantity);
    }

    public void setQuantity(int qty) {
        this.quantity.set(qty);
        this.lineTotal.set(product.getPrice() * qty);
    }

    public Product getProduct() { return product; }
    public String getSku() { return product.getSku(); }
    public String getName() { return product.getName(); }
    public double getUnitPrice() { return product.getPrice(); }

    public int getQuantity() { return quantity.get(); }
    public IntegerProperty quantityProperty() { return quantity; }

    public double getLineTotal() { return lineTotal.get(); }
    public DoubleProperty lineTotalProperty() { return lineTotal; }
}