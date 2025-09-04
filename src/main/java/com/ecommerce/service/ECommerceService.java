package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ECommerceService {

    // User Management
    public User registerUser(String name, String email, String address) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        User user = null;

        try {
            transaction = session.beginTransaction();

            // Check if email already exists
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            List<User> existingUsers = query.getResultList();

            if (!existingUsers.isEmpty()) {
                System.out.println("User with email " + email + " already exists!");
                return null;
            }

            // Create new user
            user = new User(name, email, address);
            session.save(user);

            // Create a cart for the user
            Cart cart = new Cart(user);
            session.save(cart);
            user.setCart(cart);

            transaction.commit();
            System.out.println("User registered successfully: " + user);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return user;
    }

    public User findUserByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        User user = null;

        try {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            List<User> users = query.getResultList();

            if (!users.isEmpty()) {
                user = users.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return user;
    }

    // Product Management
    public Product addProduct(String name, String description, BigDecimal price, Integer stockQuantity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Product product = null;

        try {
            transaction = session.beginTransaction();

            product = new Product(name, description, price, stockQuantity);
            session.save(product);

            transaction.commit();
            System.out.println("Product added successfully: " + product);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return product;
    }

    public List<Product> getAllProducts() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Product> products = null;

        try {
            Query<Product> query = session.createQuery("FROM Product", Product.class);
            products = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return products;
    }

    public Product findProductById(Long productId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Product product = null;

        try {
            product = session.get(Product.class, productId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return product;
    }

    // Cart Management
    public boolean addToCart(String userEmail, Long productId, Integer quantity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Find user
            User user = findUserByEmail(userEmail);
            if (user == null) {
                System.out.println("User not found!");
                return false;
            }

            // Find product
            Product product = session.get(Product.class, productId);
            if (product == null) {
                System.out.println("Product not found!");
                return false;
            }

            // Check stock
            if (product.getStockQuantity() < quantity) {
                System.out.println("Insufficient stock! Available: " + product.getStockQuantity());
                return false;
            }

            // Get user's cart
            Query<Cart> cartQuery = session.createQuery("FROM Cart WHERE user.id = :userId", Cart.class);
            cartQuery.setParameter("userId", user.getId());
            Cart cart = cartQuery.uniqueResult();

            // Check if product already in cart
            Query<CartItem> itemQuery = session.createQuery(
                    "FROM CartItem WHERE cart.id = :cartId AND product.id = :productId", CartItem.class);
            itemQuery.setParameter("cartId", cart.getId());
            itemQuery.setParameter("productId", productId);
            List<CartItem> existingItems = itemQuery.getResultList();

            if (!existingItems.isEmpty()) {
                // Update existing cart item
                CartItem existingItem = existingItems.get(0);
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                existingItem.calculateSubtotal();
                session.update(existingItem);
            } else {
                // Create new cart item
                CartItem cartItem = new CartItem(product, cart, quantity);
                session.save(cartItem);
            }

            // Update cart total
            cart.calculateTotalAmount();
            session.update(cart);

            transaction.commit();
            System.out.println("Product added to cart successfully!");
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public List<CartItem> getCartContents(String userEmail) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<CartItem> cartItems = null;

        try {
            User user = findUserByEmail(userEmail);
            if (user == null) {
                System.out.println("User not found!");
                return null;
            }

            Query<CartItem> query = session.createQuery(
                    "FROM CartItem ci WHERE ci.cart.user.email = :email", CartItem.class);
            query.setParameter("email", userEmail);
            cartItems = query.getResultList();

            // Initialize lazy loading
            for (CartItem item : cartItems) {
                item.getProduct().getName(); // Touch the product to initialize
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return cartItems;
    }

    // Order Management
    public Order placeOrder(String userEmail) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Order order = null;

        try {
            transaction = session.beginTransaction();

            // Find user
            User user = findUserByEmail(userEmail);
            if (user == null) {
                System.out.println("User not found!");
                return null;
            }

            // Get cart items
            Query<CartItem> cartQuery = session.createQuery(
                    "FROM CartItem ci WHERE ci.cart.user.email = :email", CartItem.class);
            cartQuery.setParameter("email", userEmail);
            List<CartItem> cartItems = cartQuery.getResultList();

            if (cartItems.isEmpty()) {
                System.out.println("Cart is empty!");
                return null;
            }

            // Calculate total amount
            BigDecimal totalAmount = cartItems.stream()
                    .map(CartItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Create order
            order = new Order(user, totalAmount);
            session.save(order);

            // Create order items and update stock
            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();

                // Check stock availability
                if (product.getStockQuantity() < cartItem.getQuantity()) {
                    System.out.println("Insufficient stock for product: " + product.getName());
                    transaction.rollback();
                    return null;
                }

                // Create order item
                OrderItem orderItem = new OrderItem(
                        product,
                        order,
                        cartItem.getQuantity(),
                        cartItem.getSubtotal()
                );
                session.save(orderItem);

                // Update product stock
                product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                session.update(product);

                // Remove from cart
                session.delete(cartItem);
            }

            // Update cart total
            Query<Cart> cartUpdateQuery = session.createQuery(
                    "FROM Cart WHERE user.email = :email", Cart.class);
            cartUpdateQuery.setParameter("email", userEmail);
            Cart cart = cartUpdateQuery.uniqueResult();
            cart.setTotalAmount(BigDecimal.ZERO);
            session.update(cart);

            transaction.commit();
            System.out.println("Order placed successfully: " + order);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return order;
    }

    public List<Order> getUserOrderHistory(String userEmail) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Order> orders = null;

        try {
            Query<Order> query = session.createQuery(
                    "FROM Order o WHERE o.user.email = :email ORDER BY o.orderDate DESC", Order.class);
            query.setParameter("email", userEmail);
            orders = query.getResultList();

            // Initialize lazy loading for order items
            for (Order order : orders) {
                for (OrderItem item : order.getOrderItems()) {
                    item.getProduct().getName(); // Touch to initialize
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return orders;
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<OrderItem> orderItems = null;

        try {
            Query<OrderItem> query = session.createQuery(
                    "FROM OrderItem oi WHERE oi.order.id = :orderId", OrderItem.class);
            query.setParameter("orderId", orderId);
            orderItems = query.getResultList();

            // Initialize lazy loading
            for (OrderItem item : orderItems) {
                item.getProduct().getName(); // Touch to initialize
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return orderItems;
    }
}