package ru.edu.pr04;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OrderCsvLoader {
    public record Result(int accepted, List<String> errors) {
    }

    public Result load(Path file, Order order) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        return load(lines, order);
    }

    public Result load(List<String> lines, Order order) {
        int accepted = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.isBlank() || isHeader(i, line)) {
                continue;
            }
            try {
                parseLine(line, order);
                accepted++;
            } catch (IllegalArgumentException e) {
                errors.add("line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return new Result(accepted, List.copyOf(errors));
    }

    private boolean isHeader(int index, String line) {
        return index == 0 && line.stripLeading().startsWith("productId");
    }

    private void parseLine(String line, Order order) {
        String[] parts = line.split(";", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("expected 4 fields but found " + parts.length);
        }
        double price = parsePrice(parts[2].trim());
        int quantity = parseQuantity(parts[3].trim());
        Product product = new Product(parts[0].trim(), parts[1].trim(), price);
        order.addProduct(product, quantity);
    }

    private double parsePrice(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("price has invalid format: '" + text + "'");
        }
    }

    private int parseQuantity(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("quantity has invalid format: '" + text + "'");
        }
    }
}
