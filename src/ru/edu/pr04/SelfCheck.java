package ru.edu.pr04;

import java.util.List;

public class SelfCheck {
    private static int failed = 0;

    public static void main(String[] args) {
        checkProduct();
        checkQuantityBounds();
        checkMerging();
        checkRemoval();
        checkTotal();
        checkConfirm();
        checkLoader();
        checkEncapsulation();
        System.out.println(failed == 0 ? "PASS" : "FAIL  " + failed + " checks failed");
    }

    private static void checkProduct() {
        check("product valid price 1000", new Product("P1", "Mouse", 1000).getPrice() == 1000);
        expectError("product empty id", () -> new Product("", "Mouse", 10));
        expectError("product null name", () -> new Product("P1", null, 10));
        expectError("product price zero", () -> new Product("P1", "Mouse", 0));
        expectError("product price negative", () -> new Product("P1", "Mouse", -100));
        expectError("product price NaN", () -> new Product("P1", "Mouse", Double.NaN));
    }

    private static void checkQuantityBounds() {
        Product p = new Product("P1", "Mouse", 1000);
        check("quantity lower bound 1 accepted", new OrderItem(p, 1).getQuantity() == 1);
        check("quantity upper bound " + OrderRules.MAX_QUANTITY + " accepted", new OrderItem(p, OrderRules.MAX_QUANTITY).getQuantity() == OrderRules.MAX_QUANTITY);
        expectError("quantity 0 rejected", () -> new OrderItem(p, 0));
        expectError("quantity " + (OrderRules.MAX_QUANTITY + 1) + " rejected", () -> new OrderItem(p, OrderRules.MAX_QUANTITY + 1));
        expectError("quantity negative rejected", () -> new OrderItem(p, -5));
        expectError("item without product rejected", () -> new OrderItem(null, 1));
    }

    private static void checkMerging() {
        Order order = newOrder();
        Product mouse = new Product("P1", "Mouse", 1000);
        order.addProduct(mouse, 2);
        order.addProduct(mouse, 3);
        check("repeated product merged into one line", order.getItems().size() == 1);
        check("merged quantity is 5", order.getItems().get(0).getQuantity() == 5);
        order.addProduct(new Product("P1", "Mouse", 1000), OrderRules.MAX_QUANTITY - 5);
        check("merge up to upper bound accepted", order.getItems().get(0).getQuantity() == OrderRules.MAX_QUANTITY);
        expectError("merge above upper bound rejected", () -> order.addProduct(mouse, 1));
        check("rejected merge keeps quantity", order.getItems().get(0).getQuantity() == OrderRules.MAX_QUANTITY);
        Order other = newOrder();
        other.addProduct(mouse, 1);
        expectError("same id with other price rejected", () -> other.addProduct(new Product("P1", "Mouse", 999), 1));
        expectError("same id with other name rejected", () -> other.addProduct(new Product("P1", "Mouse X", 1000), 1));
        check("conflict keeps quantity", other.getItems().get(0).getQuantity() == 1);
        expectError("null product rejected", () -> other.addProduct(null, 1));
        expectError("invalid quantity for new product rejected", () -> other.addProduct(new Product("P2", "Pad", 100), 0));
        check("invalid new line not added", other.getItems().size() == 1);
    }

    private static void checkRemoval() {
        Order order = newOrder();
        order.addProduct(new Product("P1", "Mouse", 1000), 1);
        order.addProduct(new Product("P2", "Pad", 200), 2);
        order.removeProduct("P1");
        check("product removed", order.getItems().size() == 1 && order.getItems().get(0).getProduct().getId().equals("P2"));
        expectError("removal of missing product rejected", () -> order.removeProduct("P1"));
        expectError("removal with null id rejected", () -> order.removeProduct(null));
    }

    private static void checkTotal() {
        Order order = newOrder();
        check("empty order total is 0", order.total() == 0.0);
        order.addProduct(new Product("P1", "Mouse", 1000), 2);
        order.addProduct(new Product("P2", "Pad", 250.5), 4);
        check("total is sum of lines", Math.abs(order.total() - 3002.0) < 1e-9);
        order.removeProduct("P2");
        check("total follows current items", Math.abs(order.total() - 2000.0) < 1e-9);
    }

    private static void checkConfirm() {
        OrderService service = new OrderService();
        Order empty = service.createOrder(new Employee("E1", "Test"));
        expectState("empty order cannot be confirmed", () -> service.confirmOrder(empty));
        check("empty order stays draft", empty.getStatus() == OrderStatus.DRAFT);
        Order order = service.createOrder(new Employee("E1", "Test"));
        Product mouse = new Product("P1", "Mouse", 1000);
        order.addProduct(mouse, 1);
        service.confirmOrder(order);
        check("order confirmed", order.getStatus() == OrderStatus.CONFIRMED);
        expectState("add after confirm rejected", () -> order.addProduct(mouse, 1));
        expectState("remove after confirm rejected", () -> order.removeProduct("P1"));
        expectState("second confirm rejected", order::confirm);
        check("confirmed order unchanged", order.getItems().size() == 1 && order.getItems().get(0).getQuantity() == 1);
    }

    private static void checkLoader() {
        Order order = newOrder();
        List<String> lines = List.of(
                "productId;name;price;quantity",
                "M01;Monitor 27;32990;2",
                "K01;Keyboard;4490;3",
                "M01;Monitor 27;32990;1",
                "X01;Broken;-100;1",
                "",
                "Y01;Cable;abc;1",
                "Y02;Cable;100;x",
                "Y03;;100;1",
                "Y04;Hub;100",
                "K01;Keyboard;4490;8");
        OrderCsvLoader.Result result = new OrderCsvLoader().load(lines, order);
        check("loader accepted 3 rows", result.accepted() == 3);
        check("loader reported 6 errors", result.errors().size() == 6);
        check("loader merged repeated product", order.getItems().size() == 2 && order.getItems().get(0).getQuantity() == 3);
        check("loader order total", Math.abs(order.total() - (3 * 32990.0 + 3 * 4490.0)) < 1e-9);
    }

    private static void checkEncapsulation() {
        Order order = newOrder();
        order.addProduct(new Product("P1", "Mouse", 1000), 1);
        expectUnsupported("items list is not modifiable from outside", () -> order.getItems().clear());
        check("order still has item", order.getItems().size() == 1);
    }

    private static Order newOrder() {
        return new OrderService().createOrder(new Employee("E1", "Test"));
    }

    private static void check(String name, boolean condition) {
        System.out.println((condition ? "ok    " : "FAIL  ") + name);
        if (!condition) {
            failed++;
        }
    }

    private static void expectError(String name, Runnable action) {
        expect(name, action, IllegalArgumentException.class);
    }

    private static void expectState(String name, Runnable action) {
        expect(name, action, IllegalStateException.class);
    }

    private static void expectUnsupported(String name, Runnable action) {
        expect(name, action, UnsupportedOperationException.class);
    }

    private static void expect(String name, Runnable action, Class<? extends RuntimeException> type) {
        try {
            action.run();
            check(name, false);
        } catch (RuntimeException e) {
            check(name, type.isInstance(e));
        }
    }
}
