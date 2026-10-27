package ru.edu.pr04;

import java.util.Objects;

public class Product {
    private final String id;
    private final String name;
    private final double price;

    public Product(String id, String name, double price) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product id is empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is empty");
        }
        if (Double.isNaN(price) || Double.isInfinite(price) || price <= 0) {
            throw new IllegalArgumentException("Product price must be positive: " + price);
        }
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Product)) {
            return false;
        }
        Product product = (Product) other;
        return id.equals(product.id) && name.equals(product.name) && Double.compare(price, product.price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }
}
