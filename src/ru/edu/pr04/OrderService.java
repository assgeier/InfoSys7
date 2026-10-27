package ru.edu.pr04;

import java.util.Objects;

public class OrderService {
    public Order createOrder(Employee employee) {
        return new Order(employee);
    }

    public void validateForConfirm(Order order) {
        Objects.requireNonNull(order);
        if (!order.canConfirm()) {
            throw new IllegalStateException("Order cannot be confirmed");
        }
    }

    public void confirmOrder(Order order) {
        validateForConfirm(order);
        order.confirm();
    }
}
