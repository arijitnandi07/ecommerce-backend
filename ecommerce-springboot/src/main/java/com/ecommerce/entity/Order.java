package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One relationship with User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();


    // Constructors
    public Order() {}

    public Order(User user, BigDecimal totalAmount) {
        this.user = user;
        this.orderDate = LocalDateTime.now();
        this.totalAmount = totalAmount;
    }

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }

    // Method to calculate total amount
    public void calculateTotalAmount() {
        // 1. Create a variable to hold the total, starting at zero.
        BigDecimal total = BigDecimal.ZERO;

        // 2. Loop through each item in the orderItems list.
        for (OrderItem item : this.orderItems) {
            // 3. For each item, add its price to the running total.
            total = total.add(item.getPrice());
        }

        // 4. After the loop is done, set the final total.
        this.totalAmount = total;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
