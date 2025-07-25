package com.erikm.ecommerce.repository;

import com.erikm.ecommerce.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Anotação específica para testes de JPA, configura um banco de dados em memória
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CategoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll(); // Limpa o banco antes de cada teste para garantir isolamento
    }

    @Test
    @DisplayName("Deve salvar e encontrar uma categoria pelo ID")
    void saveAndFindById() {
        Category category = new Category();
        category.setName("Livros");
        category.setDescription("Livros de ficção e não ficção");
        category.setIsActive(true);

        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory.getCategoryId());

        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getCategoryId());

        assertTrue(foundCategory.isPresent());
        assertEquals("Livros", foundCategory.get().getName());
    }

    @Test
    @DisplayName("Deve encontrar uma categoria pelo nome")
    void findByName() {
        Category category = new Category();
        category.setName("Moda");
        category.setDescription("Roupas e acessórios");
        category.setIsActive(true);
        categoryRepository.save(category);

        Optional<Category> foundCategory = categoryRepository.findByName("Moda");

        assertTrue(foundCategory.isPresent());
        assertEquals("Moda", foundCategory.get().getName());
    }

    @Test
    @DisplayName("Deve retornar vazio se a categoria não for encontrada pelo nome")
    void findByName_NotFound() {
        Optional<Category> foundCategory = categoryRepository.findByName("Inexistente");
        assertFalse(foundCategory.isPresent());
    }

    @Test
    @DisplayName("Deve retornar todas as categorias")
    void findAllCategories() {
        Category category1 = new Category();
        category1.setName("Esportes");
        category1.setDescription("Artigos esportivos");
        category1.setIsActive(true);
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Casa");
        category2.setDescription("Itens para o lar");
        category2.setIsActive(true);
        categoryRepository.save(category2);

        List<Category> categories = categoryRepository.findAll();

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertTrue(categories.stream().anyMatch(c -> c.getName().equals("Esportes")));
        assertTrue(categories.stream().anyMatch(c -> c.getName().equals("Casa")));
    }

    @Test
    @DisplayName("Deve deletar uma categoria pelo ID")
    void deleteCategory() {
        Category category = new Category();
        category.setName("Brinquedos");
        category.setDescription("Jogos e brinquedos infantis");
        category.setIsActive(true);
        Category savedCategory = categoryRepository.save(category);

        categoryRepository.deleteById(savedCategory.getCategoryId());

        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getCategoryId());
        assertFalse(foundCategory.isPresent());
    }
}