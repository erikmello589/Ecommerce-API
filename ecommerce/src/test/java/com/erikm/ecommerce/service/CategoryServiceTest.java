package com.erikm.ecommerce.service;

import com.erikm.ecommerce.dto.CategoryDTO;
import com.erikm.ecommerce.model.Category;
import com.erikm.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) 
class CategoryServiceTest {

    @Mock 
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach // Configura o ambiente antes de cada teste
    void setUp() {
        category = new Category();
        category.setCategoryId(1l);
        category.setName("Eletrônicos");
        category.setDescription("Categoria de eletrônicos em geral");
        category.setIsActive(true);

        categoryDTO = new CategoryDTO("Eletrônicos", "Categoria de eletrônicos em geral", true);
    }

    @Test
    @DisplayName("Deve criar uma nova categoria com sucesso quando o nome não existe")
    void createNewCategory_Success() {
        // Cenário: categoryRepository.findByName retorna vazio (categoria não existe)
        when(categoryRepository.findByName(categoryDTO.name())).thenReturn(Optional.empty());
        // Cenário: categoryRepository.save retorna a categoria criada
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.createNewCategory(categoryDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(category.getName(), result.getName());
        assertEquals(category.getDescription(), result.getDescription());
        assertEquals(category.getIsActive(), result.getIsActive());

        // Verifica se os métodos foram chamados corretamente
        verify(categoryRepository, times(1)).findByName(categoryDTO.name());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve lançar exceção CONFLICT ao tentar criar categoria com nome já existente")
    void createNewCategory_Conflict() {
        // Cenário: categoryRepository.findByName retorna uma categoria (categoria já existe)
        when(categoryRepository.findByName(categoryDTO.name())).thenReturn(Optional.of(category));

        // Verifica se a exceção é lançada com o status e mensagem corretos
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                categoryService.createNewCategory(categoryDTO)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Categoria já existente"));

        // Verifica se o método save NÃO foi chamado
        verify(categoryRepository, times(1)).findByName(categoryDTO.name());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve listar todas as categorias com sucesso")
    void listAllCategories_Success() {
        List<Category> categories = Arrays.asList(category, new Category());
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.listAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(categories, result);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve encontrar uma categoria pelo ID com sucesso")
    void findCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.findCategoryById(1L);

        assertNotNull(result);
        assertEquals(category.getCategoryId(), result.getCategoryId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção NOT_FOUND ao não encontrar categoria pelo ID")
    void findCategoryById_NotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                categoryService.findCategoryById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Categoria não encontrada."));
        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve editar uma categoria existente com sucesso")
    void editCategory_Success() {
        CategoryDTO updatedCategoryDTO = new CategoryDTO("Periféricos", "Teclados, mouses, etc.", false);
        Category existingCategory = new Category();
        existingCategory.setCategoryId(1L);
        existingCategory.setName("Eletrônicos");
        existingCategory.setDescription("Descrição antiga");
        existingCategory.setIsActive(true);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        Category result = categoryService.editCategory(1L, updatedCategoryDTO);

        assertNotNull(result);
        assertEquals(updatedCategoryDTO.name(), result.getName());
        assertEquals(updatedCategoryDTO.description(), result.getDescription());
        assertEquals(updatedCategoryDTO.isActive(), result.getIsActive());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    @DisplayName("Deve lançar exceção NOT_FOUND ao tentar editar categoria inexistente")
    void editCategory_NotFound() {
        CategoryDTO updatedCategoryDTO = new CategoryDTO("Periféricos", "Teclados, mouses, etc.", false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                categoryService.editCategory(99L, updatedCategoryDTO)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Categoria não encontrada"));
        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve converter Category para CategoryDTO com sucesso")
    void convertToDto_Success() {
        CategoryDTO expectedDto = new CategoryDTO("Eletrônicos", "Categoria de eletrônicos em geral", true);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(expectedDto);

        CategoryDTO result = categoryService.convertToDto(category);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(modelMapper, times(1)).map(category, CategoryDTO.class);
    }

    @Test
    @DisplayName("Deve converter CategoryDTO para Category com sucesso")
    void convertToEntity_Success() {
        Category expectedEntity = new Category();
        expectedEntity.setName("Eletrônicos");
        expectedEntity.setDescription("Categoria de eletrônicos em geral");
        expectedEntity.setIsActive(true);

        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(expectedEntity);

        Category result = categoryService.convertToEntity(categoryDTO);

        assertNotNull(result);
        assertEquals(expectedEntity, result);
        verify(modelMapper, times(1)).map(categoryDTO, Category.class);
    }
}