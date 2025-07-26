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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private Long categoryId = 1L;
    private String categoryName = "Eletrônicos";

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(categoryId);
        category.setName(categoryName);
        category.setDescription("Categoria de produtos eletrônicos");
        category.setIsActive(true);

        categoryDTO = new CategoryDTO(categoryName, "Categoria de produtos eletrônicos");
    }

    // --- createNewCategory Tests ---

    @Test
    @DisplayName("Should create a new category successfully when name is not duplicated")
    void createNewCategory_Success() {
        when(categoryRepository.findByNameAndIsActiveTrue(categoryDTO.name())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category createdCategory = categoryService.createNewCategory(categoryDTO);

        assertNotNull(createdCategory);
        assertEquals(categoryName, createdCategory.getName());
        verify(categoryRepository, times(1)).findByNameAndIsActiveTrue(categoryDTO.name());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when creating a category with a duplicated name")
    void createNewCategory_DuplicateName_ThrowsException() {
        when(categoryRepository.findByNameAndIsActiveTrue(categoryDTO.name())).thenReturn(Optional.of(category));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.createNewCategory(categoryDTO));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Categoria já existente com o nome: " + categoryDTO.name(), exception.getReason());
        verify(categoryRepository, times(1)).findByNameAndIsActiveTrue(categoryDTO.name());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // --- findAllCategories Tests ---

    @Test
    @DisplayName("Should return a page of active categories")
    void findAllCategories_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> expectedPage = new PageImpl<>(Collections.singletonList(category));
        when(categoryRepository.findByIsActiveTrue(pageable)).thenReturn(expectedPage);

        Page<Category> resultPage = categoryService.findAllCategories(pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(categoryName, resultPage.getContent().get(0).getName());
        verify(categoryRepository, times(1)).findByIsActiveTrue(pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no active categories are found")
    void findAllCategories_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> expectedPage = Page.empty(pageable);
        when(categoryRepository.findByIsActiveTrue(pageable)).thenReturn(expectedPage);

        Page<Category> resultPage = categoryService.findAllCategories(pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());
        verify(categoryRepository, times(1)).findByIsActiveTrue(pageable);
    }

    // --- findCategoryById Tests ---

    @Test
    @DisplayName("Should return a category when found by ID")
    void findCategoryById_Success() {
        when(categoryRepository.findByCategoryIdAndIsActiveTrue(categoryId)).thenReturn(Optional.of(category));

        Category foundCategory = categoryService.findCategoryById(categoryId);

        assertNotNull(foundCategory);
        assertEquals(categoryId, foundCategory.getCategoryId());
        verify(categoryRepository, times(1)).findByCategoryIdAndIsActiveTrue(categoryId);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when category is not found by ID")
    void findCategoryById_NotFound_ThrowsException() {
        when(categoryRepository.findByCategoryIdAndIsActiveTrue(categoryId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.findCategoryById(categoryId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Categoria não encontrada.", exception.getReason());
        verify(categoryRepository, times(1)).findByCategoryIdAndIsActiveTrue(categoryId);
    }

    // --- findCategoryByName Tests ---

    @Test
    @DisplayName("Should return a category when found by name")
    void findCategoryByName_Success() {
        when(categoryRepository.findByNameAndIsActiveTrue(categoryName)).thenReturn(Optional.of(category));

        Category foundCategory = categoryService.findCategoryByName(categoryName);

        assertNotNull(foundCategory);
        assertEquals(categoryName, foundCategory.getName());
        verify(categoryRepository, times(1)).findByNameAndIsActiveTrue(categoryName);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when category is not found by name")
    void findCategoryByName_NotFound_ThrowsException() {
        when(categoryRepository.findByNameAndIsActiveTrue(categoryName)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.findCategoryByName(categoryName));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Categoria não encontrada.", exception.getReason());
        verify(categoryRepository, times(1)).findByNameAndIsActiveTrue(categoryName);
    }

    // --- editCategory Tests ---

    @Test
    @DisplayName("Should edit an existing category successfully")
    void editCategory_Success() {
        String updatedName = "Eletrônicos Atualizados";
        String updatedDescription = "Descrição atualizada para eletrônicos";
        CategoryDTO updatedCategoryDTO = new CategoryDTO(updatedName, updatedDescription);

        when(categoryRepository.findByCategoryIdAndIsActiveTrue(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category editedCategory = categoryService.editCategory(categoryId, updatedCategoryDTO);

        assertNotNull(editedCategory);
        assertEquals(updatedName, editedCategory.getName());
        assertEquals(updatedDescription, editedCategory.getDescription());
        verify(categoryRepository, times(1)).findByCategoryIdAndIsActiveTrue(categoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when trying to edit a non-existent category")
    void editCategory_NotFound_ThrowsException() {
        when(categoryRepository.findByCategoryIdAndIsActiveTrue(categoryId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.editCategory(categoryId, categoryDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Categoria não encontrada.", exception.getReason());
        verify(categoryRepository, times(1)).findByCategoryIdAndIsActiveTrue(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // --- deleteCategory Tests ---

    @Test
    @DisplayName("Should logically delete a category successfully by setting isActive to false")
    void deleteCategory_Success() {
        when(categoryRepository.findByCategoryIdAndIsActiveTrue(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category deletedCategory = categoryService.deleteCategory(categoryId);

        assertNotNull(deletedCategory);
        assertFalse(deletedCategory.getIsActive());
        verify(categoryRepository, times(1)).findByCategoryIdAndIsActiveTrue(categoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when trying to delete a non-existent category")
    void deleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.findByCategoryIdAndIsActiveTrue(categoryId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.deleteCategory(categoryId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Categoria não encontrada.", exception.getReason());
        verify(categoryRepository, times(1)).findByCategoryIdAndIsActiveTrue(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // --- convertToDto Tests ---

    @Test
    @DisplayName("Should convert Category entity to CategoryDTO")
    void convertToDto_Success() {
        CategoryDTO expectedDto = new CategoryDTO(category.getName(), category.getDescription());
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(expectedDto);

        CategoryDTO resultDto = categoryService.convertToDto(category);

        assertNotNull(resultDto);
        assertEquals(expectedDto.name(), resultDto.name());
        assertEquals(expectedDto.description(), resultDto.description());
        verify(modelMapper, times(1)).map(category, CategoryDTO.class);
    }

    // --- convertToEntity Tests ---

    @Test
    @DisplayName("Should convert CategoryDTO to Category entity")
    void convertToEntity_Success() {
        Category expectedEntity = new Category();
        expectedEntity.setName(categoryDTO.name());
        expectedEntity.setDescription(categoryDTO.description());

        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(expectedEntity);

        Category resultEntity = categoryService.convertToEntity(categoryDTO);

        assertNotNull(resultEntity);
        assertEquals(expectedEntity.getName(), resultEntity.getName());
        assertEquals(expectedEntity.getDescription(), resultEntity.getDescription());
        verify(modelMapper, times(1)).map(categoryDTO, Category.class);
    }
}