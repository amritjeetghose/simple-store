package com.example.store;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StoreController {
    private final CatalogService catalogService = new CatalogService();
    private final InventoryService inventoryService = new InventoryService(catalogService);
    private final CartService cartService = new CartService(catalogService, inventoryService);
    private final OrderService orderService = new OrderService(inventoryService, cartService);

    @GetMapping("/catalog")
    public List<Product> getCatalog() {
        return catalogService.getAllProducts();
    }

    @GetMapping("/cart")
    public List<CartItem> getCart() {
        return cartService.getItems();
    }

    @GetMapping("/inventory")
    public Map<Long, Integer> getInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> payload) {
        try {
            Long productId = Long.valueOf(payload.get("productId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());
            cartService.addItem(productId, quantity);
            return ResponseEntity.ok("Added to cart");
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/cart/update")
    public ResponseEntity<String> updateCart(@RequestBody Map<String, Object> payload) {
        Long productId = Long.valueOf(payload.get("productId").toString());
        int quantity = Integer.parseInt(payload.get("quantity").toString());
        cartService.updateQuantity(productId, quantity);
        return ResponseEntity.ok("Cart updated");
    }

    @PostMapping("/cart/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long productId) {
        cartService.removeItem(productId);
        return ResponseEntity.ok("Removed from cart");
    }

    @PostMapping("/inventory/add")
    public ResponseEntity<?> addInventory(@RequestBody Map<String, Object> payload) {
        try {
            Long productId = Long.valueOf(payload.get("productId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());
            inventoryService.addStock(productId, quantity);
            return ResponseEntity.ok("Inventory updated");
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/inventory/remove")
    public ResponseEntity<?> removeInventory(@RequestBody Map<String, Object> payload) {
        try {
            Long productId = Long.valueOf(payload.get("productId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());
            inventoryService.removeStock(productId, quantity);
            return ResponseEntity.ok("Inventory updated");
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout() {
        try {
            Order order = orderService.checkout();
            return ResponseEntity.ok(order);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
