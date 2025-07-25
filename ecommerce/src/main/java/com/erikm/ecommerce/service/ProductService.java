package com.erikm.ecommerce.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.ProductDTO;
import com.erikm.ecommerce.model.Category;
import com.erikm.ecommerce.model.Product;
import com.erikm.ecommerce.repository.ProductRepository;

@Service
public class ProductService 
{
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    
    public ProductService(ProductRepository productRepository, CategoryService categoryService,
            ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    public Product createNewProduct(ProductDTO productDTO) 
    {
        Optional<Product> productFromDB = productRepository.findBySkuAndIsActiveTrue(productDTO.sku());
        
        if (productFromDB.isPresent()) 
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produto já existente com o SKU: " + productDTO.sku());
        }

        Category categoryFromDB = categoryService.findCategoryByName(productDTO.category());

        Product newProduct = new Product();
        newProduct.setName(productDTO.name());
        newProduct.setDescription(productDTO.description());
        newProduct.setPrice(productDTO.price());
        newProduct.setStockQuantity(productDTO.stockQuantity());
        newProduct.setCategory(categoryFromDB);
        newProduct.setSku(productDTO.sku());

        return productRepository.save(newProduct);
    }

    public Product findProductById(Long productId) 
    {
        return productRepository.findByProductIdAndIsActiveTrue(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));
    }

    public Product findProductBySku(String productSku) 
    {
        return productRepository.findBySkuAndIsActiveTrue(productSku)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto de SKU Nº: " + productSku + " não encontrado no sistema."));
    }

    public Page<Product> findAllProducts(Pageable pageable) 
    {
        return productRepository.findByIsActiveTrue(pageable);
    }

    public Page<Product> findProductsByCategory(String categoryName, Pageable pageable) 
    {
        return productRepository.findByCategoryNameContainingIgnoreCaseAndIsActiveTrue(categoryName, pageable);
    }


    public Product editProduct(Long productId, ProductDTO productDTO) 
    {
        Product productFromDB = productRepository.findByProductIdAndIsActiveTrue(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

        Category categoryFromDB = categoryService.findCategoryByName(productDTO.category());

        productFromDB.setName(productDTO.name());
        productFromDB.setDescription(productDTO.description());
        productFromDB.setPrice(productDTO.price());
        productFromDB.setStockQuantity(productDTO.stockQuantity());
        productFromDB.setCategory(categoryFromDB);
            
        return productRepository.save(productFromDB);
    }

    public Product editStock(Long productId, Integer stockQuantity) 
    {
        Product product =  productRepository.findByProductIdAndIsActiveTrue(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

        if (stockQuantity < 0)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque não pode ser negativo.");
        }

        product.setStockQuantity(stockQuantity);
        return productRepository.save(product);
    }

    public Product deleteProduct(Long productId) 
    {
        Product productFromDB = productRepository.findByProductIdAndIsActiveTrue(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

        productFromDB.setIsActive(false);
        return productRepository.save(productFromDB);
    }

    
    public Page<Product> findProductsByName(String name, Pageable pageable) 
    {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable);
    }

    
    public Page<Product> findProductsByCategory(Long categoryId, Pageable pageable) 
    {
        return productRepository.findByCategoryCategoryIdAndIsActiveTrue(categoryId, pageable);
    }

   
    public Page<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) 
    {
        return productRepository.findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice, pageable);
    }

    
    public Page<Product> findProductsByNameAndCategory(String name, Long categoryId, Pageable pageable) 
    {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryCategoryIdAndIsActiveTrue(name, categoryId, pageable);
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