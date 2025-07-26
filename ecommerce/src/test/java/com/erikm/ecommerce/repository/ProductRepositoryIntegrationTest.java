package com.erikm.ecommerce.repository;

import com.erikm.ecommerce.model.Category;
import com.erikm.ecommerce.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException; // Para testar unique constraints

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test") // Garante que o perfil de teste seja ativado
@DisplayName("ProductRepository Integration Tests with Category and new fields")
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository; // Precisamos do CategoryRepository para criar categorias

    private Category electronicsCategory;
    private Category booksCategory;
    private Category inactiveCategory;

    private Product product1;
    private Product product2;
    private Product product3; // Inactive product
    private Product product4; // Another product in electronics

    @BeforeEach
    void setUp() {
        productRepository.deleteAll(); // Limpa produtos
        categoryRepository.deleteAll(); // Limpa categorias

        // 1. Criar e salvar categorias
        electronicsCategory = new Category("Electronics", "Category for electronic devices", true);
        booksCategory = new Category("Books", "Category for books and literature", true);
        inactiveCategory = new Category("Furniture", "Category for home furniture", false);

        electronicsCategory = categoryRepository.save(electronicsCategory);
        booksCategory = categoryRepository.save(booksCategory);
        inactiveCategory = categoryRepository.save(inactiveCategory);

        // 2. Criar e salvar produtos associados às categorias
        product1 = new Product("Smartphone X", "Latest model smartphone", new BigDecimal("1500.00"), 50, electronicsCategory, "SMARTPHX001", true);
        product2 = new Product("The Great Novel", "A bestselling fiction book", new BigDecimal("75.50"), 120, booksCategory, "NOVELGRT001", true);
        product3 = new Product("Old Tablet", "An outdated tablet model", new BigDecimal("200.00"), 5, electronicsCategory, "OLDTAB001", false); // Inactive product
        product4 = new Product("Wireless Earbuds", "High-quality wireless earbuds", new BigDecimal("300.00"), 80, electronicsCategory, "EARBUDSW001", true);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
    }

    @Test
    @DisplayName("Should save a new product with all valid fields and timestamps")
    void shouldSaveNewProductWithAllValidFieldsAndTimestamps() {
        Category newCategory = new Category("Computers", "Category for computers", true);
        newCategory = categoryRepository.save(newCategory);

        Product newProduct = new Product("Gaming PC", "High-performance gaming computer", new BigDecimal("5000.00"), 10, newCategory, "GAMINGPC001", true);
        Product savedProduct = productRepository.save(newProduct);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getProductId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Gaming PC");
        assertThat(savedProduct.getDescription()).isEqualTo("High-performance gaming computer");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo("5000.00");
        assertThat(savedProduct.getStockQuantity()).isEqualTo(10);
        assertThat(savedProduct.getCategory()).isEqualTo(newCategory);
        assertThat(savedProduct.getSku()).isEqualTo("GAMINGPC001");
        assertThat(savedProduct.getIsActive()).isTrue();
        assertThat(savedProduct.getCreatedAt()).isNotNull();
        assertThat(savedProduct.getUpdatedAt()).isNotNull();
        assertThat(savedProduct.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(savedProduct.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should find a product by ID and isActive true")
    void shouldFindProductByIdAndIsActiveTrue() {
        Optional<Product> foundProduct = productRepository.findByProductIdAndIsActiveTrue(product1.getProductId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Smartphone X");
        assertThat(foundProduct.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not find an inactive product by ID")
    void shouldNotFindInactiveProductById() {
        Optional<Product> foundProduct = productRepository.findByProductIdAndIsActiveTrue(product3.getProductId());

        assertThat(foundProduct).isNotPresent();
    }

    @Test
    @DisplayName("Should find a product by SKU and isActive true")
    void shouldFindProductBySkuAndIsActiveTrue() {
        Optional<Product> foundProduct = productRepository.findBySkuAndIsActiveTrue("NOVELGRT001");

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("The Great Novel");
        assertThat(foundProduct.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not find an inactive product by SKU")
    void shouldNotFindInactiveProductBySku() {
        Optional<Product> foundProduct = productRepository.findBySkuAndIsActiveTrue("OLDTAB001");

        assertThat(foundProduct).isNotPresent();
    }

    @Test
    @DisplayName("Should return all active products paginated")
    void shouldReturnAllActiveProductsPaginated() {
        Pageable pageable = PageRequest.of(0, 5); // Page 0, size 5
        Page<Product> activeProductsPage = productRepository.findByIsActiveTrue(pageable);

        assertThat(activeProductsPage).isNotNull();
        assertThat(activeProductsPage.getTotalElements()).isEqualTo(3); // product1, product2, product4 are active
        assertThat(activeProductsPage.getContent()).hasSize(3);
        assertThat(activeProductsPage.getContent().stream().anyMatch(p -> p.getName().equals("Smartphone X"))).isTrue();
        assertThat(activeProductsPage.getContent().stream().anyMatch(p -> p.getName().equals("The Great Novel"))).isTrue();
        assertThat(activeProductsPage.getContent().stream().anyMatch(p -> p.getName().equals("Wireless Earbuds"))).isTrue();
        assertThat(activeProductsPage.getContent().stream().noneMatch(p -> p.getName().equals("Old Tablet"))).isTrue();
    }

    @Test
    @DisplayName("Should find products by category name containing ignore case and isActive true")
    void shouldFindProductsByCategoryNameContainingIgnoreCaseAndIsActiveTrue() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> products = productRepository.findByCategoryNameContainingIgnoreCaseAndIsActiveTrue("electro", pageable);

        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(2); // Smartphone X, Wireless Earbuds
        assertThat(products.getContent()).hasSize(2);
        assertThat(products.getContent().stream().anyMatch(p -> p.getName().equals("Smartphone X"))).isTrue();
        assertThat(products.getContent().stream().anyMatch(p -> p.getName().equals("Wireless Earbuds"))).isTrue();
        assertThat(products.getContent().stream().noneMatch(p -> p.getName().equals("Old Tablet"))).isTrue(); // Inactive product from electronics
    }

    @Test
    @DisplayName("Should find products by name containing ignore case and isActive true")
    void shouldFindProductsByNameContainingIgnoreCaseAndIsActiveTrue() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> products = productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("phone", pageable);

        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(1); // Smartphone X
        assertThat(products.getContent()).hasSize(1);
        assertThat(products.getContent().get(0).getName()).isEqualTo("Smartphone X");
    }

    @Test
    @DisplayName("Should find products by category ID and isActive true")
    void shouldFindProductsByCategoryCategoryIdAndIsActiveTrue() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> products = productRepository.findByCategoryCategoryIdAndIsActiveTrue(electronicsCategory.getCategoryId(), pageable);

        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(2); // Smartphone X, Wireless Earbuds
        assertThat(products.getContent()).hasSize(2);
        assertThat(products.getContent().stream().anyMatch(p -> p.getName().equals("Smartphone X"))).isTrue();
        assertThat(products.getContent().stream().anyMatch(p -> p.getName().equals("Wireless Earbuds"))).isTrue();
        assertThat(products.getContent().stream().noneMatch(p -> p.getName().equals("Old Tablet"))).isTrue(); // Inactive product from electronics
    }

    @Test
    @DisplayName("Should find products by price between and isActive true")
    void shouldFindProductsByPriceBetweenAndIsActiveTrue() {
        Pageable pageable = PageRequest.of(0, 5);
        // Products: Smartphone X (1500), The Great Novel (75.50), Wireless Earbuds (300)
        Page<Product> products = productRepository.findByPriceBetweenAndIsActiveTrue(new BigDecimal("100.00"), new BigDecimal("400.00"), pageable);

        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(1); // Wireless Earbuds
        assertThat(products.getContent()).hasSize(1);
        assertThat(products.getContent().get(0).getName()).isEqualTo("Wireless Earbuds");
    }

    @Test
    @DisplayName("Should find products by name containing ignore case AND category ID AND isActive true")
    void shouldFindProductsByNameContainingIgnoreCaseAndCategoryCategoryIdAndIsActiveTrue() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> products = productRepository.findByNameContainingIgnoreCaseAndCategoryCategoryIdAndIsActiveTrue("earbuds", electronicsCategory.getCategoryId(), pageable);

        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(1); // Wireless Earbuds
        assertThat(products.getContent()).hasSize(1);
        assertThat(products.getContent().get(0).getName()).isEqualTo("Wireless Earbuds");
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void shouldDeleteProductById() {
        Long productIdToDelete = product2.getProductId();
        productRepository.deleteById(productIdToDelete);

        Optional<Product> deletedProduct = productRepository.findById(productIdToDelete);
        assertThat(deletedProduct).isNotPresent();
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException for duplicate SKU")
    void shouldThrowExceptionForDuplicateSku() {
        Product duplicateSkuProduct = new Product("Duplicate SKU Product", "Some description", new BigDecimal("10.00"), 1, booksCategory, "SMARTPHX001", true);

        // Assert that saving this product throws DataIntegrityViolationException
        assertThrows(DataIntegrityViolationException.class, () -> {
            productRepository.save(duplicateSkuProduct);
        });
    }

    @Test
    @DisplayName("Should find all products (including inactive ones)")
    void shouldFindAllProducts() {
        Iterable<Product> products = productRepository.findAll();
        assertThat(products).hasSize(4); // All products (active and inactive)
    }
}