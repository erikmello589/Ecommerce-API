package com.erikm.ecommerce.service;

import com.erikm.ecommerce.dto.ProductDTO;
import com.erikm.ecommerce.model.Category;
import com.erikm.ecommerce.model.Product;
import com.erikm.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;
    private Category category;
    private Long productId = 1L;
    private String productSku = "SKU12345";
    private String productName = "Smart TV 50";
    private String categoryName = "Eletrônicos";
    private Long categoryId = 100L;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(categoryId);
        category.setName(categoryName);
        category.setDescription("Categoria de produtos eletrônicos");
        category.setIsActive(true);

        product = new Product();
        product.setProductId(productId);
        product.setName(productName);
        product.setDescription("Smart TV de alta definição");
        product.setPrice(BigDecimal.valueOf(2500.00));
        product.setStockQuantity(10);
        product.setCategory(category);
        product.setSku(productSku);
        product.setIsActive(true);

        productDTO = new ProductDTO(productName, "Smart TV de alta definição", BigDecimal.valueOf(2500.00), 10, categoryName, productSku);
    }

    // --- createNewProduct Tests ---

    @Test
    @DisplayName("Should create a new product successfully when SKU is not duplicated")
    void createNewProduct_Success() {
        when(productRepository.findBySkuAndIsActiveTrue(productDTO.sku())).thenReturn(Optional.empty());
        when(categoryService.findCategoryByName(productDTO.category())).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createNewProduct(productDTO);

        assertNotNull(createdProduct);
        assertEquals(productName, createdProduct.getName());
        assertEquals(productSku, createdProduct.getSku());
        assertEquals(categoryName, createdProduct.getCategory().getName());
        verify(productRepository, times(1)).findBySkuAndIsActiveTrue(productDTO.sku());
        verify(categoryService, times(1)).findCategoryByName(productDTO.category());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when creating a product with a duplicated SKU")
    void createNewProduct_DuplicateSku_ThrowsException() {
        when(productRepository.findBySkuAndIsActiveTrue(productDTO.sku())).thenReturn(Optional.of(product));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.createNewProduct(productDTO));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Produto já existente com o SKU: " + productDTO.sku(), exception.getReason());
        verify(productRepository, times(1)).findBySkuAndIsActiveTrue(productDTO.sku());
        verify(categoryService, never()).findCategoryByName(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when category not found during new product creation")
    void createNewProduct_CategoryNotFound_ThrowsException() {
        when(productRepository.findBySkuAndIsActiveTrue(productDTO.sku())).thenReturn(Optional.empty());
        when(categoryService.findCategoryByName(productDTO.category()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada."));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.createNewProduct(productDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Categoria não encontrada.", exception.getReason());
        verify(productRepository, times(1)).findBySkuAndIsActiveTrue(productDTO.sku());
        verify(categoryService, times(1)).findCategoryByName(productDTO.category());
        verify(productRepository, never()).save(any(Product.class));
    }

    // --- findProductById Tests ---

    @Test
    @DisplayName("Should return a product when found by ID")
    void findProductById_Success() {
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.of(product));

        Product foundProduct = productService.findProductById(productId);

        assertNotNull(foundProduct);
        assertEquals(productId, foundProduct.getProductId());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when product is not found by ID")
    void findProductById_NotFound_ThrowsException() {
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.findProductById(productId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Produto não encontrado.", exception.getReason());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
    }

    // --- findProductBySku Tests ---

    @Test
    @DisplayName("Should return a product when found by SKU")
    void findProductBySku_Success() {
        when(productRepository.findBySkuAndIsActiveTrue(productSku)).thenReturn(Optional.of(product));

        Product foundProduct = productService.findProductBySku(productSku);

        assertNotNull(foundProduct);
        assertEquals(productSku, foundProduct.getSku());
        verify(productRepository, times(1)).findBySkuAndIsActiveTrue(productSku);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when product is not found by SKU")
    void findProductBySku_NotFound_ThrowsException() {
        when(productRepository.findBySkuAndIsActiveTrue(productSku)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.findProductBySku(productSku));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Produto de SKU Nº: " + productSku + " não encontrado no sistema.", exception.getReason());
        verify(productRepository, times(1)).findBySkuAndIsActiveTrue(productSku);
    }

    // --- findAllProducts Tests ---

    @Test
    @DisplayName("Should return a page of active products")
    void findAllProducts_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findByIsActiveTrue(pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findAllProducts(pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(productName, resultPage.getContent().get(0).getName());
        verify(productRepository, times(1)).findByIsActiveTrue(pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no active products are found")
    void findAllProducts_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = Page.empty(pageable);
        when(productRepository.findByIsActiveTrue(pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findAllProducts(pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());
        verify(productRepository, times(1)).findByIsActiveTrue(pageable);
    }

    // --- findProductsByCategory (by name) Tests ---

    @Test
    @DisplayName("Should return a page of products filtered by category name")
    void findProductsByCategoryName_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findByCategoryNameContainingIgnoreCaseAndIsActiveTrue(categoryName, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByCategory(categoryName, pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(productName, resultPage.getContent().get(0).getName());
        verify(productRepository, times(1)).findByCategoryNameContainingIgnoreCaseAndIsActiveTrue(categoryName, pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no products found for category name")
    void findProductsByCategoryName_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = Page.empty(pageable);
        when(productRepository.findByCategoryNameContainingIgnoreCaseAndIsActiveTrue(categoryName, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByCategory(categoryName, pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        verify(productRepository, times(1)).findByCategoryNameContainingIgnoreCaseAndIsActiveTrue(categoryName, pageable);
    }

    // --- editProduct Tests ---

    @Test
    @DisplayName("Should edit an existing product successfully")
    void editProduct_Success() {
        String updatedName = "Smart TV 65";
        BigDecimal updatedPrice = BigDecimal.valueOf(3500.00);
        int updatedStock = 15;
        String newCategoryName = "Móveis";
        Category newCategory = new Category();
        newCategory.setCategoryId(200L);
        newCategory.setName(newCategoryName);

        ProductDTO updatedProductDTO = new ProductDTO(updatedName, "Smart TV de 65 polegadas", updatedPrice, updatedStock, newCategoryName, productSku);

        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.of(product));
        when(categoryService.findCategoryByName(newCategoryName)).thenReturn(newCategory);
        when(productRepository.save(any(Product.class))).thenReturn(product); // Mock saving the updated product

        Product editedProduct = productService.editProduct(productId, updatedProductDTO);

        assertNotNull(editedProduct);
        assertEquals(updatedName, editedProduct.getName());
        assertEquals(updatedPrice, editedProduct.getPrice());
        assertEquals(updatedStock, editedProduct.getStockQuantity());
        assertEquals(newCategoryName, editedProduct.getCategory().getName());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
        verify(categoryService, times(1)).findCategoryByName(newCategoryName);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when trying to edit a non-existent product")
    void editProduct_ProductNotFound_ThrowsException() {
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.editProduct(productId, productDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Produto não encontrado.", exception.getReason());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
        verify(categoryService, never()).findCategoryByName(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when category not found during product edit")
    void editProduct_CategoryNotFound_ThrowsException() {
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.of(product));
        when(categoryService.findCategoryByName(productDTO.category()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada."));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.editProduct(productId, productDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Categoria não encontrada.", exception.getReason());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
        verify(categoryService, times(1)).findCategoryByName(productDTO.category());
        verify(productRepository, never()).save(any(Product.class));
    }

    // --- editStock Tests ---

    @Test
    @DisplayName("Should update product stock successfully")
    void editStock_Success() {
        Integer newStockQuantity = 15;
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.editStock(productId, newStockQuantity);

        assertNotNull(updatedProduct);
        assertEquals(newStockQuantity, updatedProduct.getStockQuantity());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when trying to edit stock of a non-existent product")
    void editStock_ProductNotFound_ThrowsException() {
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.editStock(productId, 5));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Produto não encontrado.", exception.getReason());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when trying to set negative stock")
    void editStock_NegativeStock_ThrowsException() {
        Integer negativeStockQuantity = -5;
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.of(product));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.editStock(productId, negativeStockQuantity));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Estoque não pode ser negativo.", exception.getReason());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    // --- deleteProduct Tests ---

    @Test
    @DisplayName("Should logically delete a product successfully by setting isActive to false")
    void deleteProduct_Success() {
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product deletedProduct = productService.deleteProduct(productId);

        assertNotNull(deletedProduct);
        assertFalse(deletedProduct.getIsActive());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when trying to delete a non-existent product")
    void deleteProduct_NotFound_ThrowsException() {
        when(productRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.deleteProduct(productId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Produto não encontrado.", exception.getReason());
        verify(productRepository, times(1)).findByProductIdAndIsActiveTrue(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    // --- findProductsByName Tests ---

    @Test
    @DisplayName("Should return a page of products filtered by name")
    void findProductsByName_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(Collections.singletonList(product));
        String searchName = "Smart";
        when(productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchName, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByName(searchName, pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(productName, resultPage.getContent().get(0).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndIsActiveTrue(searchName, pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no products found by name")
    void findProductsByName_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = Page.empty(pageable);
        String searchName = "NonExistent";
        when(productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchName, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByName(searchName, pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndIsActiveTrue(searchName, pageable);
    }

    // --- findProductsByCategory (by ID) Tests ---

    @Test
    @DisplayName("Should return a page of products filtered by category ID")
    void findProductsByCategoryId_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findByCategoryCategoryIdAndIsActiveTrue(categoryId, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByCategory(categoryId, pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(productName, resultPage.getContent().get(0).getName());
        verify(productRepository, times(1)).findByCategoryCategoryIdAndIsActiveTrue(categoryId, pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no products found for category ID")
    void findProductsByCategoryId_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = Page.empty(pageable);
        when(productRepository.findByCategoryCategoryIdAndIsActiveTrue(categoryId, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByCategory(categoryId, pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        verify(productRepository, times(1)).findByCategoryCategoryIdAndIsActiveTrue(categoryId, pageable);
    }

    // --- findProductsByPriceRange Tests ---

    @Test
    @DisplayName("Should return a page of products within a price range")
    void findProductsByPriceRange_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal minPrice = BigDecimal.valueOf(2000.00);
        BigDecimal maxPrice = BigDecimal.valueOf(3000.00);
        Page<Product> expectedPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByPriceRange(minPrice, maxPrice, pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(productName, resultPage.getContent().get(0).getName());
        verify(productRepository, times(1)).findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice, pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no products found within price range")
    void findProductsByPriceRange_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal minPrice = BigDecimal.valueOf(100.00);
        BigDecimal maxPrice = BigDecimal.valueOf(200.00);
        Page<Product> expectedPage = Page.empty(pageable);
        when(productRepository.findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByPriceRange(minPrice, maxPrice, pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        verify(productRepository, times(1)).findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice, pageable);
    }

    // --- findProductsByNameAndCategory Tests ---

    @Test
    @DisplayName("Should return a page of products filtered by name and category ID")
    void findProductsByNameAndCategory_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchName = "Smart";
        Page<Product> expectedPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findByNameContainingIgnoreCaseAndCategoryCategoryIdAndIsActiveTrue(searchName, categoryId, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByNameAndCategory(searchName, categoryId, pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(productName, resultPage.getContent().get(0).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndCategoryCategoryIdAndIsActiveTrue(searchName, categoryId, pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no products found by name and category ID")
    void findProductsByNameAndCategory_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchName = "NonExistent";
        Page<Product> expectedPage = Page.empty(pageable);
        when(productRepository.findByNameContainingIgnoreCaseAndCategoryCategoryIdAndIsActiveTrue(searchName, categoryId, pageable)).thenReturn(expectedPage);

        Page<Product> resultPage = productService.findProductsByNameAndCategory(searchName, categoryId, pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndCategoryCategoryIdAndIsActiveTrue(searchName, categoryId, pageable);
    }

    // --- convertToDto Tests ---

    @Test
    @DisplayName("Should convert Product entity to ProductDTO")
    void convertToDto_Success() {
        ProductDTO expectedDto = new ProductDTO(product.getName(), product.getDescription(), product.getPrice(),
                product.getStockQuantity(), product.getCategory().getName(), product.getSku());
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(expectedDto);

        ProductDTO resultDto = productService.convertToDto(product);

        assertNotNull(resultDto);
        assertEquals(expectedDto.name(), resultDto.name());
        assertEquals(expectedDto.sku(), resultDto.sku());
        verify(modelMapper, times(1)).map(product, ProductDTO.class);
    }

    // --- convertToEntity Tests ---

    @Test
    @DisplayName("Should convert ProductDTO to Product entity")
    void convertToEntity_Success() {
        Product expectedEntity = new Product();
        expectedEntity.setName(productDTO.name());
        expectedEntity.setDescription(productDTO.description());
        expectedEntity.setPrice(productDTO.price());
        expectedEntity.setStockQuantity(productDTO.stockQuantity());
        expectedEntity.setSku(productDTO.sku());

        when(modelMapper.map(productDTO, Product.class)).thenReturn(expectedEntity);

        Product resultEntity = productService.convertToEntity(productDTO);

        assertNotNull(resultEntity);
        assertEquals(expectedEntity.getName(), resultEntity.getName());
        assertEquals(expectedEntity.getSku(), resultEntity.getSku());
        verify(modelMapper, times(1)).map(productDTO, Product.class);
    }
}