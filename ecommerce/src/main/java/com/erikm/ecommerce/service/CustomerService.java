package com.erikm.ecommerce.service;

import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.CustomerDTO;
import com.erikm.ecommerce.model.Customer;
import com.erikm.ecommerce.model.Role;
import com.erikm.ecommerce.repository.CustomerRepository;
import com.erikm.ecommerce.repository.RoleRepository;

@Service
public class CustomerService 
{

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper,
            RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Customer createNewCustomer(CustomerDTO customerDTO) 
    {
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());
        if (basicRole == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role básica não encontrada");
        }

        Optional<Customer> customerFromDB = customerRepository.findByDocumentAndIsActiveTrue(customerDTO.document());
        Optional<Customer> userFromEmailDB = customerRepository.findByEmailAndIsActiveTrue(customerDTO.email());

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
        newCustomer.setPassword(passwordEncoder.encode(customerDTO.password()));
        newCustomer.setRoles(Set.of(basicRole));

        return customerRepository.save(newCustomer);
    }

    public Page<Customer> listAllCostumers(Pageable pageable) 
    {
        return customerRepository.findByIsActiveTrue(pageable);

    }

    public Customer findCustomerById(Long customerId) 
    {
        return customerRepository.findByCustomerIdAndIsActiveTrue(customerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }

    public Customer findCustomerByEmail(String email) 
    {
        return customerRepository.findByEmailAndIsActiveTrue(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }

    public Customer editCustomer(Long customerId, CustomerDTO customerDTO) 
    {
        Customer customerFromDB = customerRepository.findByCustomerIdAndIsActiveTrue(customerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));

        customerFromDB.setFirstName(customerDTO.firstName());
        customerFromDB.setLastName(customerDTO.lastName());
        customerFromDB.setEmail(customerDTO.email());
        customerFromDB.setPhone(customerDTO.phone());
        customerFromDB.setDocument(customerDTO.document());

            
        return customerRepository.save(customerFromDB);
    }

    public Customer deleteCustomer(Long customerId) 
    {
        Customer customerFromDB = customerRepository.findByCustomerIdAndIsActiveTrue(customerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));

        customerFromDB.setIsActive(false);
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
