package com.example.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OrderServiceTest {

    @Test
    void checkout_should_reduce_inventory_and_create_order() {
        CatalogService catalogService = new CatalogService();
        catalogService.loadSampleProducts();

        InventoryService inventoryService = new InventoryService(catalogService);
        CartService cartService = new CartService(catalogService);
        cartService.addItem(1L, 2);

        OrderService orderService = new OrderService(inventoryService, cartService);
        Order order = orderService.checkout();

        assertEquals(1, order.getId());
        assertTrue(order.getTotal() > 0);
        assertEquals(3, inventoryService.getAvailableStock(1L));
        assertEquals(0, cartService.getSize());
    }

    @Test
    void inventory_should_support_add_and_remove_stock() {
        CatalogService catalogService = new CatalogService();
        InventoryService inventoryService = new InventoryService(catalogService);

        inventoryService.addStock(1L, 3);
        assertEquals(8, inventoryService.getAvailableStock(1L));

        inventoryService.removeStock(1L, 2);
        assertEquals(6, inventoryService.getAvailableStock(1L));
    }

    @Test
    void cart_should_not_allow_more_than_available_stock() {
        CatalogService catalogService = new CatalogService();
        CartService cartService = new CartService(catalogService);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> cartService.addItem(1L, 10)
        );

        assertTrue(exception.getMessage().contains("Not enough stock"));
    }
}
