package com.erikm.ecommerce.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.ApiResponse;
import com.erikm.ecommerce.dto.CategoryDTO;
import com.erikm.ecommerce.dto.PageResponse;
import com.erikm.ecommerce.model.Category;
import com.erikm.ecommerce.service.CategoryService;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Categoria", description = "Endpoints para gerenciamento de categorias e suas informações.")
public class CategoryController 
{
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    
    @PostMapping("/api/categories")
    public ResponseEntity<ApiResponse<?>> newCategory(@Valid @RequestBody CategoryDTO categoryDTO) 
    {
        try 
        {
            Category call = categoryService.createNewCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(call, "Categoria criada com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @GetMapping("/api/categories")
    public ResponseEntity<PageResponse<Category>> getAllCustomers(@ParameterObject Pageable pageable) 
    {
        Page<Category> call = categoryService.findAllCategories(pageable);
        PageResponse<Category> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @GetMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponse<?>> getCategory(@PathVariable("id") Long categoryId)
    {
        try 
        {
            Category call = categoryService.findCategoryById(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Categoria encontrada com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getTitleMessageCode(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @PutMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponse<?>> editCategory(@PathVariable("id") Long categoryId, @Valid @RequestBody CategoryDTO categoryDTO)
    {
        try 
        {
            Category call = categoryService.editCategory(categoryId, categoryDTO);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Categoria editada com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getTitleMessageCode(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}
