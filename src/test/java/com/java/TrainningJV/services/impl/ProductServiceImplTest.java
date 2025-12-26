package com.java.TrainningJV.services.impl;

import com.java.TrainningJV.dtos.request.ProductRequest;
import com.java.TrainningJV.mappers.ProductMapper;
import com.java.TrainningJV.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product mockProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        mockProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(100.0)
                .description("Test Description")
                .stockQuantity(50)
                .build();

        productRequest = new ProductRequest();
        productRequest.setNameProduct("New Product");
        productRequest.setPrice(200.0);
        productRequest.setDescription("New Description");
        productRequest.setStockQuantity(100);
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productMapper.selectByPrimaryKey(anyLong())).thenReturn(mockProduct);

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockProduct.getId(), result.getId());
        assertEquals(mockProduct.getName(), result.getName());
        assertEquals(mockProduct.getPrice(), result.getPrice());
        verify(productMapper, times(1)).selectByPrimaryKey(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productMapper.selectByPrimaryKey(anyLong())).thenReturn(null);

        // Act
        Product result = productService.getProductById(999L);

        // Assert
        assertNull(result);
        verify(productMapper, times(1)).selectByPrimaryKey(999L);
    }

    @Test
    void testAddProduct_Success() {
        // Arrange
        when(productMapper.insert(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(1L);
            return 1;
        });

        // Act
        long productId = productService.addProduct(productRequest);

        // Assert
        assertEquals(1L, productId);
        verify(productMapper, times(1)).insert(any(Product.class));
    }

    @Test
    void testAddProduct_Failure() {
        // Arrange
        when(productMapper.insert(any(Product.class))).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.addProduct(productRequest);
        });

        assertEquals("Product add failed", exception.getMessage());
        verify(productMapper, times(1)).insert(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        when(productMapper.selectByPrimaryKey(anyLong())).thenReturn(mockProduct);
        when(productMapper.deleteByPrimaryKey(anyLong())).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        // Assert
        verify(productMapper, times(1)).selectByPrimaryKey(1L);
        verify(productMapper, times(1)).deleteByPrimaryKey(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productMapper.selectByPrimaryKey(anyLong())).thenReturn(null);
        when(productMapper.deleteByPrimaryKey(anyLong())).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(999L);
        });

        assertEquals("Product delete failed", exception.getMessage());
        verify(productMapper, times(1)).selectByPrimaryKey(999L);
        verify(productMapper, times(1)).deleteByPrimaryKey(999L);
    }

    @Test
    void testDeleteProduct_DeleteFailed() {
        // Arrange
        when(productMapper.selectByPrimaryKey(anyLong())).thenReturn(mockProduct);
        when(productMapper.deleteByPrimaryKey(anyLong())).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(1L);
        });

        assertEquals("Product delete failed", exception.getMessage());
        verify(productMapper, times(1)).selectByPrimaryKey(1L);
        verify(productMapper, times(1)).deleteByPrimaryKey(1L);
    }

    @Test
    void testAddProduct_WithAllFields() {
        // Arrange
        ProductRequest fullRequest = new ProductRequest();
        fullRequest.setNameProduct("Complete Product");
        fullRequest.setPrice(299.99);
        fullRequest.setDescription("Complete product description");
        fullRequest.setStockQuantity(75);

        when(productMapper.insert(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(2L);
            
            // Verify all fields are set correctly
            assertEquals("Complete Product", product.getName());
            assertEquals(299.99, product.getPrice());
            assertEquals("Complete product description", product.getDescription());
            assertEquals(75, product.getStockQuantity());
            
            return 1;
        });

        // Act
        long productId = productService.addProduct(fullRequest);

        // Assert
        assertEquals(2L, productId);
        verify(productMapper, times(1)).insert(any(Product.class));
    }
}
