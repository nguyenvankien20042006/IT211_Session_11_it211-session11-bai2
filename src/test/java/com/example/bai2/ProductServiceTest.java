package com.example.bai2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Profile("dev")
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("P001", 10);
    }

    //    Thêm số lượng hợp lệ vào sản phẩm hiện có (Happy Path).
    @Test
    void increaseStockValid() {
        when(productRepository.findById("P001")).thenReturn(Optional.of(product));
        int result = productService.updateStock("P001", 5, "increase");
        assertThat(result).isEqualTo(15);
        assertThat(product.getStockQuantity()).isEqualTo(15);
    }

    //    Trừ số lượng hợp lệ từ sản phẩm hiện có (Happy Path).
    @Test
    void decreaseStockValid() {
        when(productRepository.findById("P001")).thenReturn(Optional.of(product));
        int result = productService.updateStock("P001", -5, "decrease");
        assertThat(result).isEqualTo(5);
        assertThat(product.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void decreaseStockMoreThanCurrentStock() {
        when(productRepository.findById("P001")).thenReturn(Optional.of(product));
        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateStock("P001", -15, "decrease");
        });
        verify(productRepository, never()).save(product);
    }

    @Test
    void productNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateStock("P002", 10, "increase");
        });
        verify(productRepository, never()).save(product);
    }
}
