package com.erikm.ecommerce.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.CustomerDTO;
import com.erikm.ecommerce.dto.Responses.ApiError;
import com.erikm.ecommerce.dto.Responses.ApiResponser;
import com.erikm.ecommerce.dto.Responses.PageResponse;
import com.erikm.ecommerce.model.Customer;
import com.erikm.ecommerce.service.CustomerService;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de Clientes e suas informações.")
public class CustomerController 
{

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(
        summary = "Criar um cliente.",
        description = "Dadas as credenciais requisitadas, faça a criação de um cliente no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso.", content = @Content(
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
    @PostMapping("/api/customers")
    public ResponseEntity<ApiResponser<?>> newCustomer(@RequestBody CustomerDTO customerDTO) 
    {
        try 
        {
            Customer call = customerService.createNewCustomer(customerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponser.success(call, "Cliente criado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Listar Clientes.",
        description = "Dadas as credenciais requisitadas, faça uma listagem de clientes registrados no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Listagem exibida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
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
    @GetMapping("/api/customers")
    public ResponseEntity<PageResponse<Customer>> getAllCustomers(@ParameterObject Pageable pageable) 
    {
        Page<Customer> call = customerService.listAllCostumers(pageable);
        PageResponse<Customer> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @Operation(
        summary = "Buscar um Cliente.",
        description = "Dadas as credenciais requisitadas, faça a busca de um cliente registrado no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cliente exibido com sucesso.", content = @Content(
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
    @GetMapping("/api/customers/{id}")
    public ResponseEntity<ApiResponser<?>> getCustomerbyId(@PathVariable("id") Long customerId)
    {
        try 
        {
            Customer call = customerService.findCustomerById(customerId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Cliente encontrado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Buscar um Cliente pelo e-mail.",
        description = "Dadas as credenciais requisitadas, faça a busca de um cliente registrado no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cliente exibido com sucesso.", content = @Content(
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
    @GetMapping("/api/customers/email/{email}")
    public ResponseEntity<ApiResponser<?>> getCategory(@PathVariable("email") String customerEmail)
    {
        try 
        {
            Customer call = customerService.findCustomerByEmail(customerEmail);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Cliente encontrado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Editar um Cliente.",
        description = "Dadas as credenciais requisitadas, faça a edição de um cliente registrado no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cliente editado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "404", description = "As credenciais informadas não foram encontradas no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
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
    @PutMapping("/api/customers/{id}")
    public ResponseEntity<ApiResponser<?>> editCustomer(@PathVariable("id") Long customerId, @RequestBody CustomerDTO customerDTO)
    {
        try 
        {
            Customer call = customerService.editCustomer(customerId, customerDTO);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Cliente editado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Deletar um Cliente.",
        description = "Dadas as credenciais requisitadas, faça a deleção de um cliente registrado no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cliente deletado com sucesso.", content = @Content(
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
    @DeleteMapping("/api/customers/{id}")
    public ResponseEntity<ApiResponser<?>> deleteCustomer(@PathVariable("id") Long customerId)
    {
        try 
        {
            Customer call = customerService.deleteCustomer(customerId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Cliente deletado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}
