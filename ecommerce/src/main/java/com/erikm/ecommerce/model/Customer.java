package com.erikm.ecommerce.model;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.erikm.ecommerce.dto.Responses.LoginRequest;
import com.erikm.ecommerce.model.Utils.Timestamps;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
@Table (name = "tb_customers")
public class Customer extends Timestamps
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @NotBlank(message = "O primeiro nome é obrigatório e não pode estar em branco.")
    @Size(min = 2, max = 30, message = "O primeiro nome deve ter entre 2 e 30 caracteres.")
    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    @NotBlank(message = "O sobrenome é obrigatório e não pode estar em branco.")
    @Size(min = 2, max = 30, message = "O primeiro nome deve ter entre 2 e 30 caracteres.")
    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    @NotBlank(message = "O email é obrigatório e não pode estar em branco.")
    @Email(message = "O email deve ter um formato válido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Size(max = 20, message = "O telefone não pode exceder 20 caracteres.")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "O telefone deve estar no formato válido (ex: (XX) XXXXX-XXXX).")
    @Column(name = "phone", length = 20)
    private String phone;

    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "tb_customers_roles",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Size(min = 11, max = 20, message = "O documento deve ter entre 11 e 20 caracteres.") 
    @Column(name = "document", length = 20, unique = true)
    private String document;

    @NotNull(message = "O status de atividade do cliente não pode ser nulo.")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Customer() 
    {
        super();
    }

    public Customer(String firstName, String lastName, String email, String phone, String document, Boolean isActive) {
        super(); 
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.isActive = isActive;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder passwordEncoder) {
       
        return passwordEncoder.matches(loginRequest.password(), this.password);
       
    }
}
