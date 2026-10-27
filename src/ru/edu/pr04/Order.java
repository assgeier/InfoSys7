package ru.edu.pr04;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private final Employee employee;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(Employee employee) {
        this.employee = Objects.requireNonNull(employee);
    }

    public void addProduct(Product product, int quantity) {
        ensureDraft();
        if (product == null) {
            throw new IllegalArgumentException("Product is null");
        }
        OrderItem existing = findItem(product.getId());
        if (existing == null) {
            items.add(new OrderItem(product, quantity));
            return;
        }
        if (!existing.getProduct().equals(product)) {
            throw new IllegalArgumentException("Conflicting data for product " + product.getId());
        }
        existing.increase(quantity);
    }

    public void removeProduct(String productId) {
        ensureDraft();
        OrderItem existing = productId == null ? null : findItem(productId);
        if (existing == null) {
            throw new IllegalArgumentException("Product is not in the order: " + productId);
        }
        items.remove(existing);
    }

    public double total() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getLineTotal();
        }
        return sum;
    }

    public boolean canConfirm() {
        return status == OrderStatus.DRAFT && !items.isEmpty();
    }

    public void confirm() {
        ensureDraft();
        if (items.isEmpty()) {
            throw new IllegalStateException("Order has no items");
        }
        status = OrderStatus.CONFIRMED;
    }

    public Employee getEmployee() {
        return employee;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    private void ensureDraft() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Order is already confirmed");
        }
    }

    private OrderItem findItem(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }
}
