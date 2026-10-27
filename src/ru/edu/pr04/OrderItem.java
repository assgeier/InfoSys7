package ru.edu.pr04;

public class OrderItem {
    private final Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product is null");
        }
        this.product = product;
        setQuantity(quantity);
    }

    void increase(int delta) {
        if (delta < 1) {
            throw new IllegalArgumentException("Increase must be positive: " + delta);
        }
        setQuantity((long) quantity + delta);
    }

    private void setQuantity(long quantity) {
        OrderRules.checkQuantity(quantity);
        this.quantity = (int) quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return product.getPrice() * quantity;
    }
}
