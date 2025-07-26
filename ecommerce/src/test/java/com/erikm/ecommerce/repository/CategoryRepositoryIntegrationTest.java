package com.erikm.ecommerce.repository;

import com.erikm.ecommerce.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository Integration Tests")
class CategoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll(); // Limpa o banco antes de cada teste

        category1 = new Category("Electronics", "Category for electronic devices", true);
        category2 = new Category("Books", "Category for books", true);
        category3 = new Category("Clothing", "Category for clothing items", false); // Inactive category

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
    }

    @Test
    @DisplayName("Should save a new category")
    void shouldSaveNewCategory() {
        Category newCategory = new Category("Home Appliances", "Category for home appliances", true);
        Category savedCategory = categoryRepository.save(newCategory);

        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getCategoryId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Home Appliances");
        assertThat(savedCategory.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should find a category by ID and isActive true")
    void shouldFindCategoryByIdAndIsActiveTrue() {
        Optional<Category> foundCategory = categoryRepository.findByCategoryIdAndIsActiveTrue(category1.getCategoryId());

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Electronics");
        assertThat(foundCategory.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not find an inactive category by ID")
    void shouldNotFindInactiveCategoryById() {
        Optional<Category> foundCategory = categoryRepository.findByCategoryIdAndIsActiveTrue(category3.getCategoryId());

        assertThat(foundCategory).isNotPresent();
    }

    @Test
    @DisplayName("Should find a category by name and isActive true")
    void shouldFindCategoryByNameAndIsActiveTrue() {
        Optional<Category> foundCategory = categoryRepository.findByNameAndIsActiveTrue("Books");

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Books");
        assertThat(foundCategory.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not find an inactive category by name")
    void shouldNotFindInactiveCategoryByName() {
        Optional<Category> foundCategory = categoryRepository.findByNameAndIsActiveTrue("Clothing");

        assertThat(foundCategory).isNotPresent();
    }

    @Test
    @DisplayName("Should return all active categories paginated")
    void shouldReturnAllActiveCategoriesPaginated() {
        Pageable pageable = PageRequest.of(0, 5); // Page 0, size 5
        Page<Category> activeCategoriesPage = categoryRepository.findByIsActiveTrue(pageable);

        assertThat(activeCategoriesPage).isNotNull();
        assertThat(activeCategoriesPage.getTotalElements()).isEqualTo(2); // Only category1 and category2 are active
        assertThat(activeCategoriesPage.getContent()).hasSize(2);
        assertThat(activeCategoriesPage.getContent().stream().anyMatch(c -> c.getName().equals("Electronics"))).isTrue();
        assertThat(activeCategoriesPage.getContent().stream().anyMatch(c -> c.getName().equals("Books"))).isTrue();
        assertThat(activeCategoriesPage.getContent().stream().noneMatch(c -> c.getName().equals("Clothing"))).isTrue();
    }

    @Test
    @DisplayName("Should update an existing category")
    void shouldUpdateExistingCategory() {
        category1.setDescription("Updated description for electronics");
        Category updatedCategory = categoryRepository.save(category1);

        assertThat(updatedCategory).isNotNull();
        assertThat(updatedCategory.getDescription()).isEqualTo("Updated description for electronics");
    }

    @Test
    @DisplayName("Should delete a category by ID")
    void shouldDeleteCategoryById() {
        Long categoryIdToDelete = category2.getCategoryId();
        categoryRepository.deleteById(categoryIdToDelete);

        Optional<Category> deletedCategory = categoryRepository.findById(categoryIdToDelete);
        assertThat(deletedCategory).isNotPresent();
    }

    @Test
    @DisplayName("Should find all categories")
    void shouldFindAllCategories() {
        Iterable<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(3); // All categories (active and inactive)
    }
}