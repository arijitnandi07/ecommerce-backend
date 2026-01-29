package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    public Order placeOrder(Long userId) {
        User user = userService.findById(userId);
        List<CartItem> cartItems = cartService.getCartItems(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // Create order
        Order order = new Order(user, totalAmount);
        order = orderRepository.save(order);

        // Create order items and reduce stock
        for (CartItem cartItem : cartItems) {
            // Check and reduce stock
            productService.reduceStock(cartItem.getProduct().getId(), cartItem.getQuantity());

            // Create order item
            OrderItem orderItem = new OrderItem(
                    cartItem.getProduct(),
                    order,
                    cartItem.getQuantity(),
                    cartItem.getSubtotal()
            );
            orderItemRepository.save(orderItem);
        }

        // Clear cart after successful order
        cartService.clearCart(userId);

        return order;
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    public List<Order> getUserOrders(Long userId) {
        User user = userService.findById(userId);
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public Page<Order> getUserOrdersPaged(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId, pageable);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        Order order = findById(orderId);
        return orderItemRepository.findByOrderIdWithProduct(orderId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    public void cancelOrder(Long orderId) {
        Order order = findById(orderId);

        // Restore stock for each order item
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem orderItem : orderItems) {
            productService.restoreStock(orderItem.getProduct().getId(), orderItem.getQuantity());
        }

        // Delete order
        orderRepository.delete(order);
    }
}