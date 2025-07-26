package com.erikm.ecommerce.controller;

import java.math.BigDecimal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.ProductDTO;
import com.erikm.ecommerce.dto.Responses.ApiError;
import com.erikm.ecommerce.dto.Responses.ApiResponser;
import com.erikm.ecommerce.dto.Responses.PageResponse;
import com.erikm.ecommerce.model.Product;
import com.erikm.ecommerce.service.ProductService;

import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de Produtos e suas informações.")
public class ProductController 
{
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
        summary = "Cria um novo produto.",
        description = "Cadastra um novo produto no sistema com as informações fornecidas. Endpoint disponível para administradores.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "400", description = "Os dados do produto são inválidos ou estão mal estruturados.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @PostMapping("/api/products")
    public ResponseEntity<ApiResponser<?>> newProduct(@RequestBody ProductDTO productDTO) 
    {
        try 
        {
            Product call = productService.createNewProduct(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponser.success(call, "Produto criado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Lista produtos com filtros opcionais.",
        description = "Retorna uma lista paginada de produtos, com a opção de filtrar por nome, categoria ou faixa de preço. Endpoint público para todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Listagem de produtos exibida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "Os parâmetros de filtro são inválidos.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
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
    

    @Operation(
        summary = "Busca um produto por ID.",
        description = "Retorna os detalhes de um produto específico com base no seu ID. Endpoint público para todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @GetMapping("/api/products/{id}")
    public ResponseEntity<ApiResponser<?>> getProductById(@PathVariable("id") Long productId)
    {
        try 
        {
            Product call = productService.findProductById(productId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Produto encontrado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Lista produtos por categoria.",
        description = "Retorna uma lista paginada de produtos pertencentes a uma categoria específica. Endpoint público para todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Listagem de produtos por categoria exibida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada ou sem produtos associados.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @GetMapping("/api/products/category/{categoryId}")
    public ResponseEntity<PageResponse<Product>> getProductByCategoryId(@PathVariable("categoryId") Long categoryId, @ParameterObject Pageable pageable)
    {
        Page<Product> call = productService.findProductsByCategory(categoryId, pageable);
        PageResponse<Product> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @Operation(
        summary = "Edita um produto existente.",
        description = "Atualiza as informações de um produto existente com base no seu ID. Endpoint disponível para administradores.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Produto editado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "400", description = "Os dados do produto são inválidos ou estão mal estruturados.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para edição.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @PutMapping("/api/products/{id}")
    public ResponseEntity<ApiResponser<?>> editProduct(@PathVariable("id") Long productId, @RequestBody ProductDTO productDTO)
    {
        try 
        {
            Product call = productService.editProduct(productId, productDTO);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Produto editado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Deleta um produto.",
        description = "Remove um produto do sistema com base no seu ID. Endpoint disponível para administradores.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para exclusão.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<ApiResponser<?>> deleteProduct(@PathVariable("id") Long productId)
    {
        try 
        {
            Product call = productService.deleteProduct(productId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Produto deletado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Atualiza o estoque de um produto.",
        description = "Altera a quantidade em estoque de um produto específico. Endpoint disponível para administradores.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Estoque alterado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "400", description = "A quantidade de estoque é inválida.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para atualização de estoque.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @PatchMapping("/api/products/{id}/stock")
    public ResponseEntity<ApiResponser<?>> editStock(@PathVariable("id") Long productId, @RequestParam Integer stockQuantity) 
    {
        try 
        {
            Product call = productService.editStock(productId, stockQuantity);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Estoque alterado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}