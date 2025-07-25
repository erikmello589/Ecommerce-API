package com.erikm.ecommerce.controller;

import java.math.BigDecimal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.ProductDTO;
import com.erikm.ecommerce.dto.Responses.ApiResponse;
import com.erikm.ecommerce.dto.Responses.PageResponse;
import com.erikm.ecommerce.model.Product;
import com.erikm.ecommerce.service.ProductService;

import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de Produtos e suas informações.")
public class ProductController 
{
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/api/products")
    public ResponseEntity<ApiResponse<?>> newProduct(@RequestBody ProductDTO productDTO) 
    {
        try 
        {
            Product call = productService.createNewProduct(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(call, "Produto criado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @GetMapping("/api/products")
    public ResponseEntity<PageResponse<Product>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @ParameterObject Pageable pageable) {

        Page<Product> products;

        if (name != null && categoryId != null) {
            products = productService.findProductsByNameAndCategory(name, categoryId, pageable);
        } else if (name != null) {
            products = productService.findProductsByName(name, pageable);
        } else if (categoryId != null) {
            products = productService.findProductsByCategory(categoryId, pageable);
        } else if (minPrice != null && maxPrice != null) {
            products = productService.findProductsByPriceRange(minPrice, maxPrice, pageable);
        } else {
            products = productService.findAllProducts(pageable);
        }

        // Convert Spring's Page to your custom PageResponse
        PageResponse<Product> pageResponse = PageResponse.fromSpringPage(products);

        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }
    

    @GetMapping("/api/products/{id}")
    public ResponseEntity<ApiResponse<?>> getProductById(@PathVariable("id") Long productId)
    {
        try 
        {
            Product call = productService.findProductById(productId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Produto encontrado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getTitleMessageCode(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @PutMapping("/api/products/{id}")
    public ResponseEntity<ApiResponse<?>> editProduct(@PathVariable("id") Long productId, @RequestBody ProductDTO productDTO)
    {
        try 
        {
            Product call = productService.editProduct(productId, productDTO);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Categoria editada com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getTitleMessageCode(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @PatchMapping("/api/products/{id}/stock")
    public ResponseEntity<ApiResponse<?>> editStock(@PathVariable("id") Long productId, @Valid @Min(value=0) @RequestParam Integer stockQuantity) 
    {
        try 
        {
            Product call = productService.editStock(productId, stockQuantity);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Estoque alterado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getTitleMessageCode(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}
