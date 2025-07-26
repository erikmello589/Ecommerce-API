package com.erikm.ecommerce.controller;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.OrderDTO;
import com.erikm.ecommerce.dto.Responses.ApiError;
import com.erikm.ecommerce.dto.Responses.ApiResponser;
import com.erikm.ecommerce.dto.Responses.PageResponse;
import com.erikm.ecommerce.model.Order;
import com.erikm.ecommerce.model.Enums.OrderStatus;
import com.erikm.ecommerce.service.OrderService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Pedido", description = "Endpoints para gerenciamento de Pedidos e suas informações.")
public class OrderController 
{
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
        summary = "Cria um pedido.",
        description = "Dadas as credenciais requisitadas, faça o cadastro de um pedido no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "400", description = "As credenciais informadas não são válidas ou não estão bem estruturadas.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
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
    @PostMapping("/api/orders")
    public ResponseEntity<ApiResponser<?>> newOrder(@RequestBody OrderDTO orderDTO) 
    {
        try 
        {
            Order call = orderService.createNewOrder(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponser.success(call, "Pedido criado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Listar pedidos.",
        description = "Dadas as credenciais requisitadas, faça a listagem dos pedidos no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Listagem exibida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "As credenciais informadas não são válidas ou não estão bem estruturadas.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            ))
        }
    )
    @GetMapping("/api/orders")
    public ResponseEntity<PageResponse<Order>> getAllOrders(@ParameterObject Pageable pageable) 
    {
        Page<Order> call = orderService.listAllOrders(pageable);
        PageResponse<Order> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @Operation(
        summary = "Buscar um pedido.",
        description = "Dadas as credenciais requisitadas, faça a busca de um pedido no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Pedido exibido com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "400", description = "As credenciais informadas não são válidas ou não estão bem estruturadas.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
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
    @GetMapping("/api/orders/{id}")
    public ResponseEntity<ApiResponser<?>> getOrderbyId(@PathVariable("id") Long orderId)
    {
        try 
        {
            Order call = orderService.findOrderById(orderId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Pedido Listado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Deletar um pedido.",
        description = "Dadas as credenciais requisitadas, faça a remoção de um pedido no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Pedido removido com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponser.class)
            )),
            @ApiResponse(responseCode = "400", description = "As credenciais informadas não são válidas ou não estão bem estruturadas.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
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
    @DeleteMapping("/api/orders/{id}")
    public ResponseEntity<ApiResponser<?>> deleteOrderbyId(@PathVariable("id") Long orderId)
    {
        try 
        {
            Order call = orderService.deleteOrder(orderId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Pedido cancelado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @Operation(
        summary = "Listar pedidos do cliente.",
        description = "Dadas as credenciais requisitadas, faça a listagem dos pedidos de um cliente no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Listagem exibida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "As credenciais informadas não são válidas ou não estão bem estruturadas.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
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
    @GetMapping("/api/orders/customer/{customerId}")
    public ResponseEntity<PageResponse<Order>> getOrdersbyCustomerId(@PathVariable("customerId") Long customerId, @ParameterObject Pageable pageable)
    {
        Page<Order> call = orderService.findOrdersByCustomerId(customerId, pageable);
        PageResponse<Order> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @Operation(
        summary = "Editar Status de um Pedido",
        description = "Dadas as credenciais requisitadas, altere o status de um pedido no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Alteração realizada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "As credenciais informadas não são válidas ou não estão bem estruturadas.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
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
    @PatchMapping("/api/orders/{id}/status")
    public ResponseEntity<ApiResponser<?>> editStatus(@PathVariable("id") Long orderId, @RequestParam String orderStatus) 
    {
        try 
        {
            OrderStatus call = orderService.editStatusOrder(orderId, orderStatus);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponser.success(call, "Status do Pedido alterado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponser.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}
