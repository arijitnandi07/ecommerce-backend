package com.ecommerce;

import com.ecommerce.entity.*;
import com.ecommerce.service.ECommerceService;
import com.ecommerce.util.HibernateUtil;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ECommerceApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ECommerceService service = new ECommerceService();
    private static String currentUserEmail = null;

    public static void main(String[] args) {
        System.out.println("=== Welcome to E-Commerce System ===");

        try {
            while (true) {
                if (currentUserEmail == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== Login Menu ===");
        System.out.println("1. Register New User");
        System.out.println("2. Login with Email");
        System.out.println("3. Add Sample Products (Admin)");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                loginUser();
                break;
            case 3:
                addSampleProducts();
                break;
            case 4:
                System.out.println("Thank you for using E-Commerce System!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option! Please try again.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== Main Menu === (Logged in as: " + currentUserEmail + ")");
        System.out.println("1. View All Products");
        System.out.println("2. Add Product to Cart");
        System.out.println("3. View Cart Contents");
        System.out.println("4. Place Order");
        System.out.println("5. View Order History");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                viewAllProducts();
                break;
            case 2:
                addProductToCart();
                break;
            case 3:
                viewCartContents();
                break;
            case 4:
                placeOrder();
                break;
            case 5:
                viewOrderHistory();
                break;
            case 6:
                currentUserEmail = null;
                System.out.println("Logged out successfully!");
                break;
            default:
                System.out.println("Invalid option! Please try again.");
        }
    }

    private static void registerUser() {
        System.out.println("\n=== User Registration ===");

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        User user = service.registerUser(name, email, address);
        if (user != null) {
            System.out.println("Registration successful! You can now login.");
        }
    }

    private static void loginUser() {
        System.out.println("\n=== User Login ===");

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        User user = service.findUserByEmail(email);
        if (user != null) {
            currentUserEmail = email;
            System.out.println("Login successful! Welcome, " + user.getName());
        } else {
            System.out.println("User not found! Please register first.");
        }
    }

    private static void addSampleProducts() {
        System.out.println("\n=== Adding Sample Products ===");

        service.addProduct("Laptop", "High-performance laptop", new BigDecimal("999.99"), 10);
        service.addProduct("Smartphone", "Latest model smartphone", new BigDecimal("699.99"), 15);
        service.addProduct("Headphones", "Wireless noise-canceling headphones", new BigDecimal("199.99"), 20);
        service.addProduct("Tablet", "10-inch tablet with keyboard", new BigDecimal("449.99"), 12);
        service.addProduct("Smart Watch", "Fitness tracking smartwatch", new BigDecimal("299.99"), 8);

        System.out.println("Sample products added successfully!");
    }

    private static void viewAllProducts() {
        System.out.println("\n=== All Products ===");

        List<Product> products = service.getAllProducts();
        if (products == null || products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("ID | Name | Description | Price | Stock");
        System.out.println("-".repeat(60));

        for (Product product : products) {
            System.out.printf("%d | %s | %s | $%.2f | %d%n",
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStockQuantity());
        }
    }

    private static void addProductToCart() {
        System.out.println("\n=== Add Product to Cart ===");

        viewAllProducts();

        System.out.print("Enter product ID: ");
        Long productId = getLongInput();

        System.out.print("Enter quantity: ");
        Integer quantity = getIntInput();

        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0!");
            return;
        }

        boolean success = service.addToCart(currentUserEmail, productId, quantity);
        if (!success) {
            System.out.println("Failed to add product to cart!");
        }
    }

    private static void viewCartContents() {
        System.out.println("\n=== Cart Contents ===");

        List<CartItem> cartItems = service.getCartContents(currentUserEmail);
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("Product Name | Quantity | Unit Price | Subtotal");
        System.out.println("-".repeat(55));

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            System.out.printf("%s | %d | $%.2f | $%.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.getSubtotal());
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        System.out.println("-".repeat(55));
        System.out.printf("Total Amount: $%.2f%n", totalAmount);
    }

    private static void placeOrder() {
        System.out.println("\n=== Place Order ===");

        // Show cart contents first
        viewCartContents();

        System.out.print("Do you want to place this order? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y") || confirmation.equalsIgnoreCase("yes")) {
            Order order = service.placeOrder(currentUserEmail);
            if (order != null) {
                System.out.println("Order placed successfully!");
                System.out.printf("Order ID: %d%n", order.getId());
                System.out.printf("Total Amount: $%.2f%n", order.getTotalAmount());
                System.out.printf("Order Date: %s%n",
                        order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        } else {
            System.out.println("Order cancelled.");
        }
    }

    private static void viewOrderHistory() {
        System.out.println("\n=== Order History ===");

        List<Order> orders = service.getUserOrderHistory(currentUserEmail);
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        for (Order order : orders) {
            System.out.printf("Order ID: %d | Date: %s | Total: $%.2f%n",
                    order.getId(),
                    order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    order.getTotalAmount());

            System.out.println("Items:");
            List<OrderItem> orderItems = service.getOrderItems(order.getId());
            for (OrderItem item : orderItems) {
                System.out.printf("  - %s | Quantity: %d | Price: $%.2f%n",
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice());
            }
            System.out.println("-".repeat(50));
        }
    }

    private static int getIntInput() {
        try {
            int value = Integer.parseInt(scanner.nextLine());
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format! Please enter a valid integer.");
            return -1;
        }
    }

    private static Long getLongInput() {
        try {
            Long value = Long.parseLong(scanner.nextLine());
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format! Please enter a valid number.");
            return -1L;
        }
    }
}