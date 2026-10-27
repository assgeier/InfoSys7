package ru.edu.pr04;

public final class OrderRules {
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 10;

    private OrderRules() {
    }

    public static void checkQuantity(long quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Quantity must be from " + MIN_QUANTITY + " to " + MAX_QUANTITY + ": " + quantity);
        }
    }
}
