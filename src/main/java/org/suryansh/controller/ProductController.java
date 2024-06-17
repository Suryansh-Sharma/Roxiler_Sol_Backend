package org.suryansh.controller;

import org.springframework.web.bind.annotation.*;
import org.suryansh.Dto.BarChart;
import org.suryansh.Dto.PieChart;
import org.suryansh.Dto.Statistics;
import org.suryansh.entity.Product;
import org.suryansh.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/initialize")
    public String initialize() {
        return productService.initializeProducts();
    }

    @GetMapping("/products")
    public List<Product> listTransactions(@RequestParam int month,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String search) {
        return productService.listTransactions(month, page, size, search);
    }

    @GetMapping("statistics/{month}")
    public Statistics statistics(@PathVariable int month) {
        return productService.getStatisticsForMonth(month);
    }

    @GetMapping("/pie_chart")
    public PieChart getPieChart(@RequestParam int month) {
        return productService.getPieChartForMonth(month);
    }

    @GetMapping("/bar_chart")
    public BarChart getBarChart(@RequestParam int month) {
        return productService.getBarChartForMonth(month);
    }

}
