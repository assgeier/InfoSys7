package ru.edu.pr04;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Path file = Path.of(args.length > 0 ? args[0] : "data/sample.csv");
        OrderService service = new OrderService();
        Order order = service.createOrder(new Employee("E001", "Test Employee"));
        OrderCsvLoader.Result result = new OrderCsvLoader().load(file, order);
        for (OrderItem item : order.getItems()) {
            System.out.printf("%s %s x%d = %.2f%n", item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getLineTotal());
        }
        System.out.println("Accepted rows: " + result.accepted());
        for (String error : result.errors()) {
            System.out.println("Rejected " + error);
        }
        System.out.printf("Total: %.2f%n", order.total());
        try {
            service.confirmOrder(order);
            System.out.println("Status: " + order.getStatus());
        } catch (IllegalStateException e) {
            System.out.println("Not confirmed: " + e.getMessage());
        }
    }
}
