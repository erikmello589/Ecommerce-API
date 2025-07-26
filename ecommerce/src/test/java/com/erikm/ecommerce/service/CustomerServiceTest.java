package com.erikm.ecommerce.service;

import com.erikm.ecommerce.dto.CustomerDTO;
import com.erikm.ecommerce.model.Customer;
import com.erikm.ecommerce.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;
    private Long customerId = 1L;
    private String customerEmail = "joao.silva@example.com";
    private String customerDocument = "12345678900";

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setFirstName("João");
        customer.setLastName("Silva");
        customer.setEmail(customerEmail);
        customer.setPhone("999999999");
        customer.setDocument(customerDocument);
        customer.setIsActive(true);

        customerDTO = new CustomerDTO("João", "Silva", customerEmail, "999999999", customerDocument);
    }

    // --- createNewCustomer Tests ---

    @Test
    @DisplayName("Should create a new customer successfully when email and document are not duplicated")
    void createNewCustomer_Success() {
        when(customerRepository.findByDocumentAndIsActiveTrue(customerDTO.document())).thenReturn(Optional.empty());
        when(customerRepository.findByEmailAndIsActiveTrue(customerDTO.email())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer createdCustomer = customerService.createNewCustomer(customerDTO);

        assertNotNull(createdCustomer);
        assertEquals(customerDTO.firstName(), createdCustomer.getFirstName());
        assertEquals(customerDTO.email(), createdCustomer.getEmail());
        assertEquals(customerDTO.document(), createdCustomer.getDocument());
        verify(customerRepository, times(1)).findByDocumentAndIsActiveTrue(customerDTO.document());
        verify(customerRepository, times(1)).findByEmailAndIsActiveTrue(customerDTO.email());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when creating a customer with a duplicated email")
    void createNewCustomer_DuplicateEmail_ThrowsException() {
        when(customerRepository.findByEmailAndIsActiveTrue(customerDTO.email())).thenReturn(Optional.of(customer));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.createNewCustomer(customerDTO));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Cliente já existente com o Email informado.", exception.getReason());
        verify(customerRepository, times(1)).findByEmailAndIsActiveTrue(customerDTO.email());
        verify(customerRepository, never()).findByDocumentAndIsActiveTrue(anyString()); // Should not even check document
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when creating a customer with a duplicated document")
    void createNewCustomer_DuplicateDocument_ThrowsException() {
        when(customerRepository.findByEmailAndIsActiveTrue(customerDTO.email())).thenReturn(Optional.empty()); // Email is fine
        when(customerRepository.findByDocumentAndIsActiveTrue(customerDTO.document())).thenReturn(Optional.of(customer));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.createNewCustomer(customerDTO));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Cliente já existente com o documento informado.", exception.getReason());
        verify(customerRepository, times(1)).findByEmailAndIsActiveTrue(customerDTO.email());
        verify(customerRepository, times(1)).findByDocumentAndIsActiveTrue(customerDTO.document());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // --- listAllCostumers Tests ---

    @Test
    @DisplayName("Should return a page of active customers")
    void listAllCostumers_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> expectedPage = new PageImpl<>(Collections.singletonList(customer));
        when(customerRepository.findByIsActiveTrue(pageable)).thenReturn(expectedPage);

        Page<Customer> resultPage = customerService.listAllCostumers(pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(customerEmail, resultPage.getContent().get(0).getEmail());
        verify(customerRepository, times(1)).findByIsActiveTrue(pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no active customers are found")
    void listAllCostumers_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> expectedPage = Page.empty(pageable);
        when(customerRepository.findByIsActiveTrue(pageable)).thenReturn(expectedPage);

        Page<Customer> resultPage = customerService.listAllCostumers(pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());
        verify(customerRepository, times(1)).findByIsActiveTrue(pageable);
    }

    // --- findCustomerById Tests ---

    @Test
    @DisplayName("Should return a customer when found by ID")
    void findCustomerById_Success() {
        when(customerRepository.findByCustomerIdAndIsActiveTrue(customerId)).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.findCustomerById(customerId);

        assertNotNull(foundCustomer);
        assertEquals(customerId, foundCustomer.getCustomerId());
        verify(customerRepository, times(1)).findByCustomerIdAndIsActiveTrue(customerId);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when customer is not found by ID")
    void findCustomerById_NotFound_ThrowsException() {
        when(customerRepository.findByCustomerIdAndIsActiveTrue(customerId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.findCustomerById(customerId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Cliente não encontrado.", exception.getReason());
        verify(customerRepository, times(1)).findByCustomerIdAndIsActiveTrue(customerId);
    }

    // --- findCustomerByEmail Tests ---

    @Test
    @DisplayName("Should return a customer when found by email")
    void findCustomerByEmail_Success() {
        when(customerRepository.findByEmailAndIsActiveTrue(customerEmail)).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.findCustomerByEmail(customerEmail);

        assertNotNull(foundCustomer);
        assertEquals(customerEmail, foundCustomer.getEmail());
        verify(customerRepository, times(1)).findByEmailAndIsActiveTrue(customerEmail);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when customer is not found by email")
    void findCustomerByEmail_NotFound_ThrowsException() {
        when(customerRepository.findByEmailAndIsActiveTrue(customerEmail)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.findCustomerByEmail(customerEmail));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Cliente não encontrado.", exception.getReason());
        verify(customerRepository, times(1)).findByEmailAndIsActiveTrue(customerEmail);
    }

    // --- editCustomer Tests ---

    @Test
    @DisplayName("Should edit an existing customer successfully")
    void editCustomer_Success() {
        String updatedFirstName = "Maria";
        String updatedEmail = "maria.souza@example.com";
        CustomerDTO updatedCustomerDTO = new CustomerDTO(updatedFirstName, "Souza", updatedEmail, "988888888", "00987654321");

        when(customerRepository.findByCustomerIdAndIsActiveTrue(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer editedCustomer = customerService.editCustomer(customerId, updatedCustomerDTO);

        assertNotNull(editedCustomer);
        assertEquals(updatedFirstName, editedCustomer.getFirstName());
        assertEquals(updatedEmail, editedCustomer.getEmail());
        verify(customerRepository, times(1)).findByCustomerIdAndIsActiveTrue(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when trying to edit a non-existent customer")
    void editCustomer_NotFound_ThrowsException() {
        when(customerRepository.findByCustomerIdAndIsActiveTrue(customerId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.editCustomer(customerId, customerDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Cliente não encontrado.", exception.getReason());
        verify(customerRepository, times(1)).findByCustomerIdAndIsActiveTrue(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // --- deleteCustomer Tests ---

    @Test
    @DisplayName("Should logically delete a customer successfully by setting isActive to false")
    void deleteCustomer_Success() {
        when(customerRepository.findByCustomerIdAndIsActiveTrue(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer deletedCustomer = customerService.deleteCustomer(customerId);

        assertNotNull(deletedCustomer);
        assertFalse(deletedCustomer.getIsActive());
        verify(customerRepository, times(1)).findByCustomerIdAndIsActiveTrue(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when trying to delete a non-existent customer")
    void deleteCustomer_NotFound_ThrowsException() {
        when(customerRepository.findByCustomerIdAndIsActiveTrue(customerId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.deleteCustomer(customerId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Cliente não encontrado.", exception.getReason());
        verify(customerRepository, times(1)).findByCustomerIdAndIsActiveTrue(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // --- convertToDto Tests ---

    @Test
    @DisplayName("Should convert Customer entity to CustomerDTO")
    void convertToDto_Success() {
        CustomerDTO expectedDto = new CustomerDTO(customer.getFirstName(), customer.getLastName(),
                customer.getEmail(), customer.getPhone(), customer.getDocument());
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(expectedDto);

        CustomerDTO resultDto = customerService.convertToDto(customer);

        assertNotNull(resultDto);
        assertEquals(expectedDto.firstName(), resultDto.firstName());
        assertEquals(expectedDto.email(), resultDto.email());
        verify(modelMapper, times(1)).map(customer, CustomerDTO.class);
    }

    // --- convertToEntity Tests ---

    @Test
    @DisplayName("Should convert CustomerDTO to Customer entity")
    void convertToEntity_Success() {
        Customer expectedEntity = new Customer();
        expectedEntity.setFirstName(customerDTO.firstName());
        expectedEntity.setLastName(customerDTO.lastName());
        expectedEntity.setEmail(customerDTO.email());
        expectedEntity.setPhone(customerDTO.phone());
        expectedEntity.setDocument(customerDTO.document());

        when(modelMapper.map(customerDTO, Customer.class)).thenReturn(expectedEntity);

        Customer resultEntity = customerService.convertToEntity(customerDTO);

        assertNotNull(resultEntity);
        assertEquals(expectedEntity.getFirstName(), resultEntity.getFirstName());
        assertEquals(expectedEntity.getEmail(), resultEntity.getEmail());
        verify(modelMapper, times(1)).map(customerDTO, Customer.class);
    }
}