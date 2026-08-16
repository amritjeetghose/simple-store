package com.example.store;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private final CatalogService catalogService;
    private final Map<Long, Integer> reservedStock = new HashMap<>();

    public InventoryService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public int getAvailableStock(Long productId) {
        Product product = catalogService.getProduct(productId);
        if (product == null) {
            return 0;
        }
        return product.getStock() - reservedStock.getOrDefault(productId, 0);
    }

    public boolean reserveStock(Long productId, int quantity) {
        Product product = catalogService.getProduct(productId);
        if (product == null || quantity <= 0) {
            return false;
        }

        int available = getAvailableStock(productId);
        if (available < quantity) {
            return false;
        }

        reservedStock.merge(productId, quantity, Integer::sum);
        return true;
    }

    public void releaseStock(Long productId, int quantity) {
        reservedStock.computeIfPresent(productId, (id, current) -> Math.max(0, current - quantity));
    }

    public void confirmSale(Long productId, int quantity) {
        Product product = catalogService.getProduct(productId);
        if (product == null) {
            return;
        }
        int updated = Math.max(0, product.getStock() - quantity);
        product.setStock(updated);
        releaseStock(productId, quantity);
    }

    public void addStock(Long productId, int quantity) {
        Product product = catalogService.getProduct(productId);
        if (product == null || quantity <= 0) {
            return;
        }
        product.setStock(product.getStock() + quantity);
    }

    public void removeStock(Long productId, int quantity) {
        Product product = catalogService.getProduct(productId);
        if (product == null || quantity <= 0) {
            return;
        }

        if (product.getStock() < quantity) {
            throw new IllegalStateException("Cannot remove more stock than available for product " + product.getName());
        }

        product.setStock(product.getStock() - quantity);
    }

    public Map<Long, Integer> getAllInventory() {
        Map<Long, Integer> inventory = new HashMap<>();
        for (Product product : catalogService.getAllProducts()) {
            inventory.put(product.getId(), product.getStock());
        }
        return inventory;
    }
}
