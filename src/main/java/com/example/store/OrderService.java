package com.example.store;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {
    private final InventoryService inventoryService;
    private final CartService cartService;
    private final AtomicLong sequence = new AtomicLong(1);
    private final List<Order> orders = new ArrayList<>();

    public OrderService(InventoryService inventoryService, CartService cartService) {
        this.inventoryService = inventoryService;
        this.cartService = cartService;
    }

    public Order checkout() {
        List<CartItem> cartItems = cartService.getItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        for (CartItem item : cartItems) {
            if (!inventoryService.reserveStock(item.getProductId(), item.getQuantity())) {
                throw new IllegalStateException("Insufficient stock for " + item.getProductName());
            }
        }

        double total = cartItems.stream().mapToDouble(CartItem::getLineTotal).sum();
        long orderId = sequence.getAndIncrement();
        Order order = new Order(orderId, new ArrayList<>(cartItems), total, "PAID");

        for (CartItem item : cartItems) {
            inventoryService.confirmSale(item.getProductId(), item.getQuantity());
        }

        orders.add(order);
        cartService.clear();
        return order;
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }
}
