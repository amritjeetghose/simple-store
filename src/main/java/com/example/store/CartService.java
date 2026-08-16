package com.example.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CartService {
    private final CatalogService catalogService;
    private final InventoryService inventoryService;
    private final Map<Long, CartItem> items = new ConcurrentHashMap<>();

    public CartService(CatalogService catalogService) {
        this(catalogService, null);
    }

    public CartService(CatalogService catalogService, InventoryService inventoryService) {
        this.catalogService = catalogService;
        this.inventoryService = inventoryService;
    }

    public void addItem(Long productId, int quantity) {
        Product product = catalogService.getProduct(productId);
        if (product == null || quantity <= 0) {
            return;
        }

        int availableStock = inventoryService != null
            ? inventoryService.getAvailableStock(productId)
            : product.getStock();

        int existingQuantity = items.getOrDefault(productId, new CartItem()).getQuantity();
        int totalRequested = existingQuantity + quantity;

        if (totalRequested > availableStock) {
            throw new IllegalStateException("Not enough stock available for " + product.getName());
        }

        CartItem existing = items.get(productId);
        if (existing == null) {
            items.put(productId, new CartItem(productId, product.getName(), quantity, product.getPrice()));
        } else {
            existing.setQuantity(totalRequested);
        }
    }

    public void removeItem(Long productId) {
        items.remove(productId);
    }

    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeItem(productId);
            return;
        }

        Product product = catalogService.getProduct(productId);
        if (product == null) {
            return;
        }

        int availableStock = inventoryService != null
            ? inventoryService.getAvailableStock(productId)
            : product.getStock();

        if (quantity > availableStock) {
            throw new IllegalStateException("Not enough stock available for " + product.getName());
        }

        CartItem existing = items.get(productId);
        if (existing != null) {
            existing.setQuantity(quantity);
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public int getSize() {
        return items.size();
    }

    public double getTotal() {
        return items.values().stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    public void clear() {
        items.clear();
    }
}
