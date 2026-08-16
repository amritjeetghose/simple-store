package com.example.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CatalogService {
    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    public CatalogService() {
        loadSampleProducts();
    }

    public void loadSampleProducts() {
        products.clear();
        products.put(1L, new Product(1L, "Laptop", 899.99, 5));
        products.put(2L, new Product(2L, "Mouse", 29.99, 15));
        products.put(3L, new Product(3L, "Keyboard", 49.99, 12));
        products.put(4L, new Product(4L, "Monitor", 249.99, 7));
        products.put(5L, new Product(5L, "USB Cable", 9.99, 30));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product getProduct(Long id) {
        return products.get(id);
    }

    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    public void updateStock(Long productId, int stock) {
        Product product = products.get(productId);
        if (product != null) {
            product.setStock(stock);
        }
    }
}
