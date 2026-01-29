package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-One relationship with User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CartItem> cartItems = new ArrayList<>();


    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Constructors
    public Cart() {}

    public Cart(User user) {
        this.user = user;
        this.totalAmount = BigDecimal.ZERO;
    }

    // Method to calculate total amount
    public void calculateTotalAmount() {
        // 1. Create a variable to hold the total, starting at zero.
        BigDecimal total = BigDecimal.ZERO;

        // 2. Loop through each item in the cartItems list.
        for (CartItem item : this.cartItems) {
            // 3. For each item, add its subtotal to the running total.
            total = total.add(item.getSubtotal());
        }

        // 4. After the loop is done, set the final total.
        this.totalAmount = total;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
