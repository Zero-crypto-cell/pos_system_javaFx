package com.example.possystem.model;

import javafx.beans.property.*;

public class Product {
    private final StringProperty sku;
    private final StringProperty name;
    private final StringProperty category;
    private final DoubleProperty price;
    private final IntegerProperty stock;

    public Product(String sku, String name, String category, double price, int stock) {
        this.sku = new SimpleStringProperty(sku);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
    }

    public String getSku() { return sku.get(); }
    public StringProperty skuProperty() { return sku; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }

    public double getPrice() { return price.get(); }
    public void setPrice(double value) { price.set(value); }
    public DoubleProperty priceProperty() { return price; }

    public int getStock() { return stock.get(); }
    public void setStock(int value) { stock.set(value); }
    public IntegerProperty stockProperty() { return stock; }
}