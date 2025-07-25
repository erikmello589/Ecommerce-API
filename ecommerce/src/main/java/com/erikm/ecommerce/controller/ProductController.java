package com.erikm.ecommerce.controller;

import java.util.UUID;

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

import com.erikm.ecommerce.dto.ApiResponse;
import com.erikm.ecommerce.dto.ProductDTO;
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
