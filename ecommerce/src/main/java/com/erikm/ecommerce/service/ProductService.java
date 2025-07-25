package com.erikm.ecommerce.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.ProductDTO;
import com.erikm.ecommerce.model.Product;
import com.erikm.ecommerce.repository.ProductRepository;

@Service
public class ProductService 
{
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public Product createNewProduct(ProductDTO productDTO) 
    {
        Optional<Product> productFromDB = productRepository.findBySku(productDTO.sku());
        
        if (productFromDB.isPresent()) 
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produto já existente com o SKU: " + productDTO.sku());
        }

        Product newProduct = new Product();
        newProduct.setName(productDTO.name());
        newProduct.setDescription(productDTO.description());
        newProduct.setIsActive(productDTO.isActive());

        return productRepository.save(newProduct);
    }

    public Product findProductById(Long productId) 
    {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));
    }

    public Page<Product> findProductsByCategory(String categoryName, Pageable pageable) 
    {
        return productRepository.findByCategoryNameContainingIgnoreCase(categoryName, pageable);
    }

    public Product editProduct(Long productId, ProductDTO productDTO) 
    {
        Product productFromDB = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

        productFromDB.setName(productDTO.name());
        productFromDB.setDescription(productDTO.description());
        productFromDB.setPrice(productDTO.price());
        productFromDB.setStockQuantity(productDTO.stockQuantity());
        //productFromDB.setCategory(productDTO.categoryDTO()); 
        //TODO: tratar edição de categoria (pode se criar uma nova OU adicionar uma já existente no banco)
        productFromDB.setIsActive(productDTO.isActive());
            
        return productRepository.save(productFromDB);
    }

    public Product editStock(Long productId, Integer stockQuantity) 
    {
        if (stockQuantity < 0)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque não pode ser negativo.");
        }

        Product productFromDB = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

        productFromDB.setStockQuantity(stockQuantity);
        return productRepository.save(productFromDB);
    }

    public ProductDTO convertToDto(Product product) 
    {
        return modelMapper.map(product, ProductDTO.class);
    }

    public Product convertToEntity(ProductDTO productDTO) 
    {
        return modelMapper.map(productDTO, Product.class);
    }
}