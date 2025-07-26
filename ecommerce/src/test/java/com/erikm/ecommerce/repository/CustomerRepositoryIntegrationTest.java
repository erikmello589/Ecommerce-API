package com.erikm.ecommerce.repository;

import com.erikm.ecommerce.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException; // Importar para testar unique constraints

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test") // Garante que o perfil de teste seja ativado
@DisplayName("CustomerRepository Integration Tests with new fields and Timestamps")
class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer1;
    private Customer customer2;
    private Customer customer3; // Inactive customer

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll(); // Limpa o banco antes de cada teste

        customer1 = new Customer("João", "Silva", "joao.silva@example.com", "(11) 98765-4321", "111.111.111-11", true);
        customer2 = new Customer("Maria", "Souza", "maria.souza@example.com", "(21) 91234-5678", "222.222.222-22", true);
        customer3 = new Customer("Carlos", "Pereira", "carlos.pereira@example.com", "(31) 99887-6543", "333.333.333-33", false);

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
    }

    @Test
    @DisplayName("Should save a new customer with all valid fields and timestamps")
    void shouldSaveNewCustomerWithAllValidFieldsAndTimestamps() {
        Customer newCustomer = new Customer("Ana", "Costa", "ana.costa@example.com", "(41) 95555-4444", "444.444.444-44", true);
        Customer savedCustomer = customerRepository.save(newCustomer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getCustomerId()).isNotNull();
        assertThat(savedCustomer.getFirstName()).isEqualTo("Ana");
        assertThat(savedCustomer.getLastName()).isEqualTo("Costa");
        assertThat(savedCustomer.getEmail()).isEqualTo("ana.costa@example.com");
        assertThat(savedCustomer.getPhone()).isEqualTo("(41) 95555-4444");
        assertThat(savedCustomer.getDocument()).isEqualTo("444.444.444-44");
        assertThat(savedCustomer.getIsActive()).isTrue();
        assertThat(savedCustomer.getCreatedAt()).isNotNull();
        assertThat(savedCustomer.getUpdatedAt()).isNotNull();
        assertThat(savedCustomer.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(savedCustomer.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should find a customer by ID and isActive true")
    void shouldFindCustomerByIdAndIsActiveTrue() {
        Optional<Customer> foundCustomer = customerRepository.findByCustomerIdAndIsActiveTrue(customer1.getCustomerId());

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getFirstName()).isEqualTo("João");
        assertThat(foundCustomer.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not find an inactive customer by ID")
    void shouldNotFindInactiveCustomerById() {
        Optional<Customer> foundCustomer = customerRepository.findByCustomerIdAndIsActiveTrue(customer3.getCustomerId());

        assertThat(foundCustomer).isNotPresent();
    }

    @Test
    @DisplayName("Should find a customer by document and isActive true")
    void shouldFindCustomerByDocumentAndIsActiveTrue() {
        Optional<Customer> foundCustomer = customerRepository.findByDocumentAndIsActiveTrue("222.222.222-22");

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getFirstName()).isEqualTo("Maria");
        assertThat(foundCustomer.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not find an inactive customer by document")
    void shouldNotFindInactiveCustomerByDocument() {
        Optional<Customer> foundCustomer = customerRepository.findByDocumentAndIsActiveTrue("333.333.333-33");

        assertThat(foundCustomer).isNotPresent();
    }

    @Test
    @DisplayName("Should find a customer by email and isActive true")
    void shouldFindCustomerByEmailAndIsActiveTrue() {
        Optional<Customer> foundCustomer = customerRepository.findByEmailAndIsActiveTrue("joao.silva@example.com");

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getFirstName()).isEqualTo("João");
        assertThat(foundCustomer.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not find an inactive customer by email")
    void shouldNotFindInactiveCustomerByEmail() {
        Optional<Customer> foundCustomer = customerRepository.findByEmailAndIsActiveTrue("carlos.pereira@example.com");

        assertThat(foundCustomer).isNotPresent();
    }

    @Test
    @DisplayName("Should return all active customers paginated")
    void shouldReturnAllActiveCustomersPaginated() {
        Pageable pageable = PageRequest.of(0, 5); // Page 0, size 5
        Page<Customer> activeCustomersPage = customerRepository.findByIsActiveTrue(pageable);

        assertThat(activeCustomersPage).isNotNull();
        assertThat(activeCustomersPage.getTotalElements()).isEqualTo(2); // Only customer1 and customer2 are active
        assertThat(activeCustomersPage.getContent()).hasSize(2);
        assertThat(activeCustomersPage.getContent().stream().anyMatch(c -> c.getFirstName().equals("João"))).isTrue();
        assertThat(activeCustomersPage.getContent().stream().anyMatch(c -> c.getFirstName().equals("Maria"))).isTrue();
        assertThat(activeCustomersPage.getContent().stream().noneMatch(c -> c.getFirstName().equals("Carlos"))).isTrue();
    }

    @Test
    @DisplayName("Should delete a customer by ID")
    void shouldDeleteCustomerById() {
        Long customerIdToDelete = customer2.getCustomerId();
        customerRepository.deleteById(customerIdToDelete);

        Optional<Customer> deletedCustomer = customerRepository.findById(customerIdToDelete);
        assertThat(deletedCustomer).isNotPresent();
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException for duplicate email")
    void shouldThrowExceptionForDuplicateEmail() {
        Customer duplicateEmailCustomer = new Customer("Pedro", "Lima", "joao.silva@example.com", "(51) 91111-2222", "555.555.555-55", true);

        // Assert that saving this customer throws DataIntegrityViolationException
        assertThrows(DataIntegrityViolationException.class, () -> {
            customerRepository.save(duplicateEmailCustomer);
        });
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException for duplicate document")
    void shouldThrowExceptionForDuplicateDocument() {
        Customer duplicateDocumentCustomer = new Customer("Fernanda", "Gomes", "fernanda.gomes@example.com", "(61) 93333-4444", "111.111.111-11", true);

        // Assert that saving this customer throws DataIntegrityViolationException
        assertThrows(DataIntegrityViolationException.class, () -> {
            customerRepository.save(duplicateDocumentCustomer);
        });
    }

    @Test
    @DisplayName("Should find all customers")
    void shouldFindAllCustomers() {
        Iterable<Customer> customers = customerRepository.findAll();
        assertThat(customers).hasSize(3); // All customers (active and inactive)
    }
}