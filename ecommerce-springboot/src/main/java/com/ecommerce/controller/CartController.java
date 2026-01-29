package com.ecommerce.controller;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.ApiResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<ApiResponse<CartItem>> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            CartItem cartItem = cartService.addToCart(userId, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(
                    ApiResponse.success("Product added to cart successfully", cartItem)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Cart>> getCart(@PathVariable Long userId) {
        try {
            Cart cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(
                    ApiResponse.success("Cart retrieved successfully", cart)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    @GetMapping("/items/{userId}")
    public ResponseEntity<ApiResponse<List<CartItem>>> getCartItems(@PathVariable Long userId) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(userId);
            return ResponseEntity.ok(
                    ApiResponse.success("Cart items retrieved successfully", cartItems)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItem>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        try {
            CartItem updatedItem = cartService.updateCartItemQuantity(cartItemId, quantity);
            if (updatedItem == null) {
                return ResponseEntity.ok(
                        ApiResponse.success("Item removed from cart")
                );
            }
            return ResponseEntity.ok(
                    ApiResponse.success("Cart item updated successfully", updatedItem)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@PathVariable Long cartItemId) {
        try {
            cartService.removeFromCart(cartItemId);
            return ResponseEntity.ok(
                    ApiResponse.success("Item removed from cart successfully")
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok(
                    ApiResponse.success("Cart cleared successfully")
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }
}