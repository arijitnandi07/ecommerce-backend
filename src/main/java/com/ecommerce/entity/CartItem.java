package com.ecommerce.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One relationship with Product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Many-to-One relationship with Cart
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Constructors
    public CartItem() {}

    public CartItem(Product product, Cart cart, Integer quantity) {
        this.product = product;
        this.cart = cart;
        this.quantity = quantity;
        this.calculateSubtotal();
    }

    // Method to calculate subtotal
    public void calculateSubtotal() {
        if (product != null && quantity != null) {
            this.subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.calculateSubtotal();
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.calculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                '}';
    }
}