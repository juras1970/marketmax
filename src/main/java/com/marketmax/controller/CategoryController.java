package com.marketmax.controller;

import com.marketmax.dto.CategoryDTO;
import com.marketmax.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Categorias de produtos do marketplace")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar categorias com contagem de produtos")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }
}
