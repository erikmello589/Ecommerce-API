package com.erikm.ecommerce.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.CustomerDTO;
import com.erikm.ecommerce.model.Customer;
import com.erikm.ecommerce.repository.CustomerRepository;

@Service
public class CustomerService 
{

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    public Customer createNewCustomer(CustomerDTO customerDTO) 
    {
        Optional<Customer> customerFromDB = customerRepository.findByDocument(customerDTO.document());
        Optional<Customer> userFromEmailDB = customerRepository.findByEmail(customerDTO.email());

        if (userFromEmailDB.isPresent()) 
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cliente já existente com o Email informado.");
        }

        if (customerFromDB.isPresent()) 
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cliente já existente com o documento informado.");
        }

        Customer newCustomer = new Customer();
        newCustomer.setFirstName(customerDTO.firstName());
        newCustomer.setLastName(customerDTO.lastName());
        newCustomer.setEmail(customerDTO.email());
        newCustomer.setPhone(customerDTO.phone());
        newCustomer.setDocument(customerDTO.document());

        return customerRepository.save(newCustomer);
    }

    public Page<Customer> listAllCostumers(Pageable pageable) 
    {
        return customerRepository.findAll(pageable);

    }

    public Customer findCustomerById(Long customerId) 
    {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }

    public Customer findCustomerByEmail(String email) 
    {
        return customerRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }

    public Customer editCustomer(Long customerId, CustomerDTO customerDTO) 
    {
        Customer customerFromDB = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clinete não encontrado."));

        customerFromDB.setFirstName(customerDTO.firstName());
        customerFromDB.setLastName(customerDTO.lastName());
        customerFromDB.setEmail(customerDTO.email());
        customerFromDB.setPhone(customerDTO.phone());
        customerFromDB.setDocument(customerDTO.document());

            
        return customerRepository.save(customerFromDB);
    }

    public CustomerDTO convertToDto(Customer customer) 
    {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    public Customer convertToEntity(CustomerDTO customerDTO) 
    {
        return modelMapper.map(customerDTO, Customer.class);
    }
}
