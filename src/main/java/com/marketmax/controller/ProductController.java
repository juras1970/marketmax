package com.marketmax.controller;

import com.marketmax.dto.ProductDTO;
import com.marketmax.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento de produtos do marketplace")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna lista de produtos com filtros opcionais por busca e categoria")
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(productService.findAll(q, category));
    }

    @GetMapping("/promotions")
    @Operation(summary = "Listar produtos em promoção", description = "Retorna produtos que possuem desconto ativo")
    public ResponseEntity<List<ProductDTO>> getPromotions() {
        return ResponseEntity.ok(productService.findPromotions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }
}
