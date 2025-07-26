package com.erikm.ecommerce.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import com.erikm.ecommerce.dto.Responses.LoginRequest;
import com.erikm.ecommerce.dto.Responses.LoginResponse;
import com.erikm.ecommerce.model.Customer;
import com.erikm.ecommerce.model.Role;
import com.erikm.ecommerce.repository.CustomerRepository;


import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenService(JwtEncoder jwtEncoder, CustomerRepository customerRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) 
    {
        Optional<Customer> customer = customerRepository.findByEmailAndIsActiveTrue(loginRequest.email());

        if (customer.isEmpty() || !customer.get().isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("email ou senha inválida");
        }

        return generateTokens(customer.get());
    }

    public LoginResponse refreshToken(JwtAuthenticationToken refreshToken) {
        Customer customer = customerRepository.findById(Long.valueOf(refreshToken.getName()))
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        return generateTokens(customer);
    }

    private LoginResponse generateTokens(Customer customer) {
        var now = Instant.now();
        var accessTokenExpiresIn = 900L; // 15 minutos

        // Gerar Access Token (contém apenas informações mínimas)
        String accessTokenScopes = customer.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(customer.getCustomerId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpiresIn))
                .claim("scope", accessTokenScopes)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();

        // Gerar Refresh Token (informações mínimas e separadas)
        var refreshTokenExpiresIn = 1296000L; // 15 dias
        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(customer.getCustomerId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenExpiresIn))
                .build();

        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        return new LoginResponse(accessToken, accessTokenExpiresIn, refreshToken);
    }
}
