package com.erikm.ecommerce.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.CategoryDTO;
import com.erikm.ecommerce.dto.Responses.ApiError;
import com.erikm.ecommerce.dto.Responses.ApiResponser;
import com.erikm.ecommerce.dto.Responses.PageResponse;
import com.erikm.ecommerce.model.Category;
import com.erikm.ecommerce.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Categoria", description = "Endpoints para gerenciamento de categorias e suas informações.")
public class CategoryController 
{
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
        summary = "Crie uma categoria.",
        description = "Dadas as credenciais requisitadas, faça o cadastro de uma categoria no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "409", description = "As credenciais informadas estão em conflito com dados já existentes no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @PostMapping("/api/categories")
    public ResponseEntity<ApiResponser<?>> newCategory(@RequestBody CategoryDTO categoryDTO) 
    {
        try 
        {
            Category call = categoryService.createNewCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponser.success(call, "Categoria criada com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Listar categorias.",
        description = "Dadas as credenciais requisitadas, faça uma listagem de categorias registradas no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Categoria listada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )),
            @ApiResponse(responseCode = "404", description = "As credenciais informadas não foram encontradas no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @GetMapping("/api/categories")
    public ResponseEntity<PageResponse<?>> getAllCategories(@ParameterObject Pageable pageable) 
    {
        Page<Category> call = categoryService.findAllCategories(pageable);
        PageResponse<Category> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @Operation(
        summary = "Buscar categoria.",
        description = "Dadas as credenciais requisitadas, faça uma busca de categoria registrada no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "404", description = "As credenciais informadas não foram encontradas no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @GetMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponser<?>> getCategory(@PathVariable("id") Long categoryId)
    {
        try 
        {
            Category call = categoryService.findCategoryById(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Categoria encontrada com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Editar categoria.",
        description = "Dadas as credenciais requisitadas, faça uma edição de categoria registrada no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "404", description = "As credenciais informadas não foram encontradas no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @PutMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponser<?>> editCategory(@PathVariable("id") Long categoryId, @RequestBody CategoryDTO categoryDTO)
    {
        try 
        {
            Category call = categoryService.editCategory(categoryId, categoryDTO);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Categoria editada com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Deletar categoria.",
        description = "Dadas as credenciais requisitadas, faça uma deleção de categoria registrada no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Categoria deletada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "404", description = "As credenciais informadas não foram encontradas no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @DeleteMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponser<?>> deleteCategory(@PathVariable("id") Long categoryId)
    {
        try 
        {
            Category call = categoryService.deleteCategory(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Categoria deletada com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}
