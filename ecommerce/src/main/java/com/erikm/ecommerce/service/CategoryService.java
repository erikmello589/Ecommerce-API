package com.erikm.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.CategoryDTO;
import com.erikm.ecommerce.model.Category;
import com.erikm.ecommerce.model.Product;
import com.erikm.ecommerce.repository.CategoryRepository;

import org.modelmapper.ModelMapper;

@Service
public class CategoryService 
{
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public Category createNewCategory(CategoryDTO categoryDTO) 
    {
        Optional<Category> categoryFromDB = categoryRepository.findByName(categoryDTO.name());

        if (categoryFromDB.isPresent()) 
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria já existente com o nome: " + categoryDTO.name());
        }

        Category newCategory = new Category();
        newCategory.setName(categoryDTO.name());
        newCategory.setDescription(categoryDTO.description());

        return categoryRepository.save(newCategory);
    }

    public List<Category> findAllCategories() 
    {
        return categoryRepository.findAll();
        //TODO Regra de negócios para Auth: Usuário Admin receberá todas as informações (incluindo timestamps) e usuário deslogado apenas receberá DTOs

    }

    public Category findCategoryById(Long categoryId) 
    {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada."));
    }

    public Category findCategoryByName(String categoryName) 
    {
        return categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada."));
    }

    public Category editCategory(Long categoryId, CategoryDTO categoryDTO) 
    {
        Category categoryFromDB = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        categoryFromDB.setName(categoryDTO.name());
        categoryFromDB.setDescription(categoryDTO.description());
            
        return categoryRepository.save(categoryFromDB);
    }

    public CategoryDTO convertToDto(Category category) 
    {
        return modelMapper.map(category, CategoryDTO.class);
    }

    public Category convertToEntity(CategoryDTO categoryDTO) 
    {
        return modelMapper.map(categoryDTO, Category.class);
    }

}