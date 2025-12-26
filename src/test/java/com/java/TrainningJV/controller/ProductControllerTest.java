package com.java.TrainningJV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.TrainningJV.dtos.request.ProductRequest;
import com.java.TrainningJV.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetProductById_Success() throws Exception {
        // Arrange
        Long productId = 1L;

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get product successfully"))
                .andExpect(jsonPath("$.data").value("Product with ID: 1 retrieved successfully"));
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setNameProduct("Test Product");
        productRequest.setPrice(100.0);
        productRequest.setDescription("Test Description");
        productRequest.setStockQuantity(50);

        when(productService.addProduct(any(ProductRequest.class))).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Created product successfully"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        // Arrange
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.message").value("Deleted product successfully"))
                .andExpect(jsonPath("$.data").value(1));
    }
}
