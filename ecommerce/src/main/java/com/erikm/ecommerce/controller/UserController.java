package com.erikm.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.CreateUserDTO;
import com.erikm.ecommerce.service.UserService;
import com.erikm.ecommerce.dto.Responses.ApiError;
import com.erikm.ecommerce.dto.Responses.ApiResponser;
import com.erikm.ecommerce.model.User;

@RestController
@Tag(name = "Usuário", description = "Endpoints para gerenciamento de Usuários e suas informações.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Crie uma conta de usuário.",
        description = "Dadas as credenciais requisitadas, faça um cadastro de um Usuário simples no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Conta de usuário criada com sucesso.", content = @Content(
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
    @PostMapping("/api/user/register")
    public ResponseEntity<ApiResponser<?>> newUser(@RequestBody CreateUserDTO createUserDTO) 
    {
        try 
        {
            User call = userService.registerNewUser(createUserDTO);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Usuário criado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}