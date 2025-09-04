package com.ecommerce.entity;

import javax.persistence.*;
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
    private User user;

    // One-to-Many relationship with CartItems
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
        this.totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", user=" + (user != null ? user.getName() : "null") +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + cartItems.size() +
                '}';
    }
}