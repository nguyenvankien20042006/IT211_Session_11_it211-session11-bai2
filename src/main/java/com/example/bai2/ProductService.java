package com.example.bai2;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public int updateStock(String productId, int quantityChange, String type) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException(
                    "Product not found with ID: " + productId);
        }

        Product product = productOpt.get();
        int currentStock = product.getStockQuantity();

        if (type.equals("increase")) {
            quantityChange = Math.abs(quantityChange);

        } else if (type.equals("decrease")) {
            quantityChange = -Math.abs(quantityChange);
        }

        int newStock = currentStock + quantityChange;
        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Resulting stock would be negative");
        }
        product.setStockQuantity(newStock);
        productRepository.save(product);
        return newStock;
    }
}