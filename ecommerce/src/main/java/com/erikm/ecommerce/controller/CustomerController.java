package com.erikm.ecommerce.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.ApiResponse;
import com.erikm.ecommerce.dto.CustomerDTO;
import com.erikm.ecommerce.dto.PageResponse;
import com.erikm.ecommerce.model.Customer;
import com.erikm.ecommerce.service.CustomerService;

import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de Clientes e suas informações.")
public class CustomerController 
{

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/api/customers")
    public ResponseEntity<ApiResponse<?>> newCustomer(@Valid @RequestBody CustomerDTO customerDTO) 
    {
        try 
        {
            Customer call = customerService.createNewCustomer(customerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(call, "Cliente criado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @GetMapping("/api/customers")
    public ResponseEntity<PageResponse<Customer>> getAllCustomers(@ParameterObject Pageable pageable) 
    {
        Page<Customer> call = customerService.listAllCostumers(pageable);
        PageResponse<Customer> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @GetMapping("/api/customers/{id}")
    public ResponseEntity<ApiResponse<?>> getCustomerbyId(@PathVariable("id") Long customerId)
    {
        try 
        {
            Customer call = customerService.findCustomerById(customerId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Cliente encontrado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getTitleMessageCode(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @GetMapping("/api/customers/email/{email}")
    public ResponseEntity<ApiResponse<?>> getCategory(@PathVariable("email") String customerEmail)
    {
        try 
        {
            Customer call = customerService.findCustomerByEmail(customerEmail);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Cliente encontrado com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getTitleMessageCode(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @PutMapping("/api/customers/{id}")
    public ResponseEntity<ApiResponse<?>> editCustomer(@PathVariable("id") Long customerId, @Valid @RequestBody CustomerDTO customerDTO)
    {
        try 
        {
            Customer call = customerService.editCustomer(customerId, customerDTO);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Categoria editada com sucesso."));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getTitleMessageCode(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}
